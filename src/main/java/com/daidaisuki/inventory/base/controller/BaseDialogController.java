package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public abstract class BaseDialogController<R, VM extends BaseDialogViewModel<R>> {
  protected Stage dialogStage;
  protected boolean confirmed = false;
  protected final VM viewModel;

  @FXML protected Button confirmButton;

  protected BaseDialogController(VM viewModel) {
    this.viewModel = viewModel;
  }

  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }

  public boolean isConfirmed() {
    return this.confirmed;
  }

  public R getResult() {
    return confirmed ? this.viewModel.createResult() : null;
  }

  protected void showError(String message) {
    AlertHelper.showErrorAlert(dialogStage, "Invalid Input", "Please fix input errors", message);
  }

  @FXML
  protected void handleConfirm() {
    if (!this.viewModel.isInvalidProperty().get()) {
      this.confirmed = true;
      this.dialogStage.close();
    } else {
      AlertHelper.showWarningAlert(
          dialogStage, "Invalid Input", null, "Please check the required fields.");
    }
  }

  @FXML
  protected void handleCancel() {
    this.dialogStage.close();
  }
}
