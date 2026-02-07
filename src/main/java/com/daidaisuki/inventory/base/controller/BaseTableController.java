package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.ui.dialog.DialogService;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.sql.SQLException;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

public abstract class BaseTableController<T, VM extends BaseListViewModel<T>> {
  @FXML protected TableView<T> table;
  @FXML protected Button addButton;
  @FXML protected Button editButton;
  @FXML protected Button deleteButton;
  protected VM viewModel;
  private DialogService dialogService;
  private EventHandler<KeyEvent> escapeFilter =
      event -> {
        if (event.getCode() == KeyCode.ESCAPE && !event.isConsumed()) {
          this.clearSelection();
          event.consume();
        }
      };

  protected BaseTableController(VM viewModel) {
    this.viewModel = viewModel;
  }

  protected void initializeBase() {
    this.viewModel
        .selectedItemProperty()
        .bind(this.table.getSelectionModel().selectedItemProperty());
    this.addButton.disableProperty().bind(this.viewModel.isBusyProperty());
    BooleanBinding nothingSelected = this.viewModel.selectedItemProperty().isNull();
    this.editButton.disableProperty().bind(nothingSelected);
    this.deleteButton.disableProperty().bind(nothingSelected);
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
    this.setupDeselectOnEmptySpace(this.table);
    this.table
        .sceneProperty()
        .addListener(
            (obs, oldScene, newScene) -> {
              if (oldScene != null) {
                oldScene.removeEventFilter(KeyEvent.KEY_PRESSED, this.escapeFilter);
              }
              if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this.escapeFilter);
              }
            });
    this.viewModel.refresh();
  }

  protected void setupDeselectOnEmptySpace(TableView<?> targetTable) {
    targetTable.setOnMouseClicked(
        event -> {
          Node target = (Node) event.getTarget();
          while (target != null && target != targetTable) {
            if (target instanceof TableCell<?, ?> cell) {
              if (!cell.getTableRow().isEmpty()) {
                return;
              }
            }
            if (target.getStyleClass().contains("column-header-background")) {
              return;
            }
            target = target.getParent();
          }
          targetTable.getSelectionModel().clearSelection();
        });
  }

  protected void setupEscapeHandler(Node node, Runnable action) {
    if (node == null) {
      return;
    }
    node.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (event.getCode() == KeyCode.ESCAPE) {
            action.run();
            event.consume();
          }
        });
  }

  protected void clearSelection() {
    if (this.table != null && this.table.getSelectionModel() != null) {
      this.table.getSelectionModel().clearSelection();
    }
  }

  protected DialogService getDialogService() {
    if (dialogService == null) {
      this.dialogService = new DialogService(this.getWindow());
    }
    return dialogService;
  }

  protected Window getWindow() {
    return FxWindowUtils.getWindow(this.table);
  }

  @FXML
  protected void handleDelete() {
    T selected = this.viewModel.selectedItemProperty().get();
    if (selected != null) {
      String message = getDeleteConfirmationMessage(selected);
      boolean confirmed =
          AlertHelper.showConfirmationAlert(
              this.getWindow(), "Confirm Delete", message, "This item will be deactivated.");
      if (confirmed) {
        this.viewModel.delete(selected);
      }
    }
  }

  protected String getDeleteConfirmationMessage(T item) {
    return "Are you sure you want to delete this item?";
  }
}
