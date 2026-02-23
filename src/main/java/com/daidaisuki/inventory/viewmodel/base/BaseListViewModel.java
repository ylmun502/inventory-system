package com.daidaisuki.inventory.viewmodel.base;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;

public abstract class BaseListViewModel<T> {
  protected final ObservableList<T> dataList = FXCollections.observableArrayList();
  protected final FilteredList<T> filteredList;
  protected final SortedList<T> sortedList;

  protected final ObjectProperty<T> selectedItem = new SimpleObjectProperty<>();
  protected final BooleanProperty isBusy = new SimpleBooleanProperty(false);
  protected final BooleanProperty isLoading = new SimpleBooleanProperty(false);
  protected final BooleanProperty showArchived = new SimpleBooleanProperty(false);
  protected final StringProperty searchFilter = new SimpleStringProperty("");

  private Consumer<Throwable> errorReporter;

  public BaseListViewModel() {
    this.filteredList = new FilteredList<>(this.dataList);
    this.sortedList = new SortedList<>(this.filteredList);
    this.bindFilteredList();
  }

  private void bindFilteredList() {
    this.filteredList
        .predicateProperty()
        .bind(
            Bindings.createObjectBinding(
                () -> {
                  boolean showingArchived = this.showArchived.get();
                  String filterText =
                      Optional.ofNullable(searchFilter.get()).orElse("").trim().toLowerCase();
                  return (T item) -> {
                    if (isArchived(item) != showingArchived) {
                      return false;
                    }
                    if (filterText.isEmpty()) {
                      return true;
                    }
                    return matchesSearch(item, filterText);
                  };
                },
                this.showArchived,
                this.searchFilter));
  }

  protected abstract List<T> fetchItems() throws Exception;

  protected abstract boolean matchesSearch(T item, String filterText);

  protected abstract boolean isArchived(T item);

  public abstract void add(T item);

  public abstract void update(T item);

  public abstract void delete(T item);

  public void refresh() {
    executeLoadingTask(this::fetchItems, this.dataList::setAll);
  }

  protected <V> void executeLoadingTask(Callable<V> worker, Consumer<V> onSuccess) {
    internalExecute(worker, onSuccess, this.isLoading);
  }

  protected <V> void executeTask(Callable<V> worker, Consumer<V> onSuccess) {
    internalExecute(worker, onSuccess, this.isBusy);
  }

  private <V> void internalExecute(
      Callable<V> worker, Consumer<V> onSuccess, BooleanProperty state) {
    if (state.get()) {
      return;
    }
    state.set(true);
    Task<V> task =
        new Task<>() {
          @Override
          protected V call() throws Exception {
            return worker.call();
          }
        };
    task.setOnSucceeded(
        e -> {
          state.set(false);
          if (onSuccess != null) {
            onSuccess.accept(task.getValue());
          }
        });
    task.setOnFailed(
        e -> {
          state.set(false);
          this.handleError(task.getException());
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
          this.refresh();
        });
  }

  @FunctionalInterface
  public interface TaskAction {
    void run() throws Exception;
  }

  public void handleError(Throwable exception) {
    if (this.errorReporter != null) {
      Platform.runLater(() -> this.errorReporter.accept(exception));
    } else {
      // May change to logger in the future.
      exception.printStackTrace();
    }
  }

  public void setOnError(Consumer<Throwable> handler) {
    this.errorReporter = handler;
  }

  public final ObjectProperty<T> selectedItemProperty() {
    return this.selectedItem;
  }

  public final BooleanProperty isBusyProperty() {
    return this.isBusy;
  }

  public final BooleanProperty isLoadingProperty() {
    return this.isLoading;
  }

  public final BooleanProperty showArchivedProperty() {
    return this.showArchived;
  }

  public final StringProperty searchFilterProperty() {
    return this.searchFilter;
  }

  public final SortedList<T> getSortedList() {
    return this.sortedList;
  }
}
