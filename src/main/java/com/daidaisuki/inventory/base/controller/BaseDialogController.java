package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public abstract class BaseDialogController<T, VM extends BaseDialogViewModel<T>> {
  protected Stage dialogStage;
  protected boolean saveClicked = false;
  protected VM viewModel;

  @FXML protected Button saveButton;

  protected BaseDialogController(VM viewModel) {
    this.viewModel = viewModel;
  }

  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }

  public boolean isSaveClicked() {
    return this.saveClicked;
  }

  protected void setupBaseBinding() {
    if (this.saveButton != null && this.viewModel != null) {
      this.saveButton.disableProperty().bind(this.viewModel.isInvalidProperty());
    }
  }

  protected void showError(String message) {
    AlertHelper.showErrorAlert(dialogStage, "Invalid Input", "Please fix input errors", message);
  }

  protected void closeDialog() {
    if (dialogStage != null) {
      dialogStage.close();
    }
  }

  public abstract T getModel();

  public abstract void setModel(T model);

  @FXML
  protected void handleCancel() {
    closeDialog();
  }

  @FXML
  protected abstract void handleSave();
}
