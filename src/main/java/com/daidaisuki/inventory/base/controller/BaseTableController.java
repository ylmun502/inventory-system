package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.user.AppSession;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.sql.SQLException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

public abstract class BaseTableController<T, VM extends BaseListViewModel<T>> {
  @FXML private Label userLabel;
  @FXML protected TableView<T> table;
  protected VM viewModel;
  private EventHandler<KeyEvent> escapeFilter =
      event -> {
        if (event.getCode() == KeyCode.ESCAPE && !event.isConsumed()) {
          Node focusOwner = this.table.getScene().getFocusOwner();
          if (focusOwner instanceof TextInputControl textInput) {
            if (!textInput.getText().isEmpty()) {
              textInput.clear();
            } else {
              this.clearSelection();
              this.table.requestFocus();
            }
            event.consume();
          } else {
            this.clearSelection();
            event.consume();
          }
        }
      };

  protected BaseTableController(VM viewModel) {
    this.viewModel = viewModel;
  }

  protected void initializeBaseTableController() {
    this.setupTableDataBinding();
    this.bindSelectionModel();
    this.setupDeselectOnEmptySpace(this.table);
    this.setupSceneKeyFilter();
    this.bindViewModelProperties();
    this.initializeBaseUI();
    this.viewModel.refresh();
  }

  protected void setupTableDataBinding() {
    if (this.table != null) {
      this.viewModel.getSortedList().comparatorProperty().bind(this.table.comparatorProperty());
      this.table.setItems(this.viewModel.getSortedList());
    }
  }

  private void bindSelectionModel() {
    this.viewModel
        .selectedItemProperty()
        .bind(this.table.getSelectionModel().selectedItemProperty());
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

  private void setupSceneKeyFilter() {
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
  }

  protected void bindViewModelProperties() {
    this.viewModel.setOnError(this::handleError);
  }

  protected void handleError(Throwable exception) {
    // Remove sout after testing
    System.out.println("task wrapped outermost exception: " + exception);
    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
    if (cause.getCause() instanceof SQLException) {
      AlertHelper.showDatabaseError(
          this.getWindow(), "A database operation failed", cause.getMessage());
    } else if (cause instanceof DataAccessException dataAcessException) {
      AlertHelper.showWarningAlert(
          getWindow(), "Persistence Error", "Action failed", dataAcessException.getMessage());
    } else {
      AlertHelper.showErrorAlert(
          getWindow(), "System Error", "An unexpected error occurred", cause.getMessage());
    }
  }

  protected void initializeBaseUI() {
    if (this.userLabel != null) {
      this.userLabel.setText("User: " + AppSession.getInstance().getUserName());
    }
  }

  protected void clearSelection() {
    if (this.table != null && this.table.getSelectionModel() != null) {
      this.table.getSelectionModel().clearSelection();
    }
  }

  protected Window getWindow() {
    return FxWindowUtils.getWindow(this.table);
  }
}
