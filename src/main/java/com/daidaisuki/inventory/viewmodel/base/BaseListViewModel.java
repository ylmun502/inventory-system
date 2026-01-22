package com.daidaisuki.inventory.viewmodel.base;

import java.util.List;
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

  protected abstract List<T> fetchItems() throws Exception;

  public abstract void add(T item) throws Exception;

  public abstract void update(T item) throws Exception;

  public abstract void delete(T item) throws Exception;

  public void loadData() {
    isBusy.set(true);
    Task<List<T>> task =
        new Task<List<T>>() {
          @Override
          protected List<T> call() throws Exception {
            return fetchItems();
          }
        };
    task.setOnSucceeded(
        e -> {
          dataList.setAll(task.getValue());
          isBusy.set(false);
        });
    task.setOnFailed(
        e -> {
          isBusy.set(false);
          task.getException().printStackTrace();
        });
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  public final ObservableList<T> getDataList() {
    return dataList;
  }

  public final ObjectProperty<T> selectedItemProperty() {
    return selectedItem;
  }

  public final BooleanProperty isBusyProperty() {
    return isBusy;
  }
}
