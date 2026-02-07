package com.daidaisuki.inventory.viewmodel.base;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public abstract class BaseListViewModel<T> {
  protected final ObservableList<T> dataList = FXCollections.observableArrayList();
  protected final ObjectProperty<T> selectedItem = new SimpleObjectProperty<>();
  protected final BooleanProperty isBusy = new SimpleBooleanProperty(false);
  protected final BooleanProperty isLoading = new SimpleBooleanProperty(false);
  private Consumer<Throwable> errorReporter;

  protected abstract List<T> fetchItems() throws Exception;

  public abstract void add(T item);

  public abstract void update(T item);

  public abstract void delete(T item);

  @FunctionalInterface
  public interface TaskAction {
    void run() throws Exception;
  }

  protected <V> void executeLoadingTask(Callable<V> worker, Consumer<V> onSuccess) {
    if (this.isLoading.get()) {
      return;
    }
    this.isLoading.set(true);
    Task<V> task =
        new Task<>() {
          @Override
          protected V call() throws Exception {
            return worker.call();
          }
        };
    task.setOnSucceeded(
        e -> {
          this.isLoading.set(false);
          if (onSuccess != null) {
            onSuccess.accept(task.getValue());
          }
        });
    task.setOnFailed(
        e -> {
          this.isLoading.set(false);
          handleError(task.getException());
        });
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  protected void runAsync(TaskAction action, Runnable onSucceeded) {
    executeTask(
        () -> {
          action.run();
          return null;
        },
        result -> {
          if (onSucceeded != null) {
            onSucceeded.run();
          }
          refresh();
        });
  }

  public void refresh() {
    executeTask(this::fetchItems, dataList::setAll);
  }

  private <V> void executeTask(Callable<V> worker, Consumer<V> onSuccess) {
    if (this.isBusy.get()) {
      return;
    }
    this.isBusy.set(true);
    Task<V> task =
        new Task<>() {
          @Override
          protected V call() throws Exception {
            return worker.call();
          }
        };
    task.setOnSucceeded(
        e -> {
          this.isBusy.set(false);
          if (onSuccess != null) {
            onSuccess.accept(task.getValue());
          }
        });
    task.setOnFailed(
        e -> {
          this.isBusy.set(false);
          handleError(task.getException());
        });
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  public void handleError(Throwable exception) {
    if (errorReporter != null) {
      Platform.runLater(() -> errorReporter.accept(exception));
    } else {
      // May change to logger in the future.
      exception.printStackTrace();
    }
  }

  public void setOnError(Consumer<Throwable> handler) {
    this.errorReporter = handler;
  }

  public final ObservableList<T> getDataList() {
    return dataList;
  }

  public final void setSelectedItem(T item) {
    selectedItem.set(item);
  }

  public final ObjectProperty<T> selectedItemProperty() {
    return selectedItem;
  }

  public final BooleanProperty isBusyProperty() {
    return isBusy;
  }

  public final BooleanProperty isLoadingProperty() {
    return isLoading;
  }
}
