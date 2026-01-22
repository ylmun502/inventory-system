package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.util.ViewLoader;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Pair;

public abstract class BaseTableController<T, VM extends BaseListViewModel<T>> {
  @FXML protected TableView<T> table;
  @FXML protected Button addButton;
  @FXML protected Button editButton;
  @FXML protected Button deleteButton;
  protected VM viewModel;

  protected BaseTableController(VM viewModel) {
    this.viewModel = viewModel;
  }

  protected void initializeBase(
      TableView<T> table, Button addButton, Button editButton, Button deleteButton) {
    this.table = table;
    this.addButton = addButton;
    this.editButton = editButton;
    this.deleteButton = deleteButton;
    this.table.setItems(this.viewModel.getDataList());
    this.viewModel
        .selectedItemProperty()
        .bind(this.table.getSelectionModel().selectedItemProperty());
    BooleanBinding nothingSelected = this.viewModel.selectedItemProperty().isNull();
    this.editButton.disableProperty().bind(nothingSelected);
    this.deleteButton.disableProperty().bind(nothingSelected);
    this.addButton.disableProperty().bind(this.viewModel.isBusyProperty());
    this.viewModel.setOnError(
        exception -> {
          if (exception instanceof SQLException sqlException) {
            AlertHelper.showDatabaseError(this.getWindow(), "Database Error", sqlException);
          } else if (exception instanceof InsufficientStockException) {
            AlertHelper.showWarningAlert(
                getWindow(), "Stock Warning", null, exception.getMessage());
          } else {
            AlertHelper.showErrorAlert(
                getWindow(),
                "System Error",
                "An unexpected error occurred",
                exception.getMessage());
          }
        });
    this.viewModel.refresh();
  }

  protected <R, C extends BaseDialogController<R, ?>> R showDialog(
      Class<C> controllerClass, DialogView dialogView, Object viewModel) {
    try {
      Callback<Class<?>, Object> factory =
          type -> {
            if (type == controllerClass) {
              return newInstance(controllerClass, viewModel);
            }
            return null;
          };
      Pair<Parent, C> pair = ViewLoader.loadViewWithControllerFactory(dialogView, factory);
      C controller = pair.getValue();

      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(this.getWindow());
      dialogStage.setScene(new Scene(pair.getKey()));
      controller.setDialogStage(dialogStage);
      dialogStage.showAndWait();
      return controller.getResult();
    } catch (Exception e) {
      this.viewModel.handleError(e);
      return null;
    }
  }

  private <C, V> C newInstance(Class<C> controllerClass, V viewModel) {
    if (viewModel == null) {
      return null;
    }
    try {
      for (Constructor<?> constructor : controllerClass.getConstructors()) {
        if (constructor.getParameterCount() == 1) {
          Class<?> paramType = constructor.getParameterTypes()[0];
          if (paramType.isAssignableFrom(viewModel.getClass())) {
            Object instance = constructor.newInstance(viewModel);
            return controllerClass.cast(instance);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  protected Window getWindow() {
    return FxWindowUtils.getWindow(this.table);
  }

  protected void deleteItem(T itemToDelete) {
    boolean confirmed =
        AlertHelper.showConfirmationAlert(
            this.getWindow(),
            "Delete Item",
            "Are you sure you want to delete this item?",
            "This action can not be undone.");
    if (confirmed) {
      this.viewModel.delete(itemToDelete);
    }
  }
}

/* MVC Structure
public abstract class BaseTableController<T> {
  protected TableView<T> table;
  protected Button addButton, editButton, deleteButton;

  protected ObservableList<T> dataList = FXCollections.observableArrayList();

  protected abstract List<T> fetchFromDB() throws SQLException;

  protected abstract void addItem(T item) throws SQLException;

  protected abstract void updateItem(T item) throws SQLException;

  protected abstract void deleteItem(T item) throws SQLException;

  protected abstract T showDialog(T itemToEdit);

  protected abstract Window getWindow();

  protected void initializeBase(
      TableView<T> table, Button addButton, Button editButton, Button deleteButton) {
    this.table = table;
    this.addButton = addButton;
    this.editButton = editButton;
    this.deleteButton = deleteButton;

    table.setItems(dataList);

    table
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              boolean selected = newVal != null;
              editButton.setDisable(!selected);
              deleteButton.setDisable(!selected);
            });

    editButton.setDisable(true);
    deleteButton.setDisable(true);

    try {
      refreshTable();
    } catch (SQLException e) {
      AlertHelper.showDatabaseError(getWindow(), "Unable to load data.", e);
    }
  }

  protected void refreshTable() throws SQLException {
    dataList.setAll(fetchFromDB());
    table.sort();
  }

  @FXML
  protected void handleAdd() {
    T newItem = showDialog(null);
    if (newItem != null) {
      FxUiUtils.runWithButtonsDisabled(
          () -> {
            try {
              addItem(newItem);
              refreshTable();
            } catch (SQLException e) {
              AlertHelper.showDatabaseError(getWindow(), "Could not add item.", e);
            }
          },
          addButton,
          editButton,
          deleteButton);
    }
  }

  @FXML
  protected void handleEdit() {
    T selected = table.getSelectionModel().getSelectedItem();
    if (selected == null) {
      AlertHelper.showSelectionRequiredAlert(getWindow(), "edit");
      return;
    }
    T edited = showDialog(selected);
    if (edited != null) {
      FxUiUtils.runWithButtonsDisabled(
          () -> {
            try {
              updateItem(edited);
              refreshTable();
            } catch (SQLException e) {
              AlertHelper.showDatabaseError(getWindow(), "Could not edit item.", e);
            }
          },
          addButton,
          editButton,
          deleteButton);
    }
  }

  @FXML
  protected void handleDelete() {
    T selected = table.getSelectionModel().getSelectedItem();
    if (selected == null) {
      AlertHelper.showSelectionRequiredAlert(getWindow(), "delete");
      return;
    }
    boolean confirmed =
        AlertHelper.showConfirmationAlert(
            getWindow(), "Delete", null, "Are you sure you want to delete this item?");
    if (confirmed) {
      FxUiUtils.runWithButtonsDisabled(
          () -> {
            try {
              deleteItem(selected);
              refreshTable();
            } catch (SQLException e) {
              AlertHelper.showDatabaseError(getWindow(), "Could not delete item.", e);
            }
          },
          addButton,
          editButton,
          deleteButton);
    }
  }
}
*/
