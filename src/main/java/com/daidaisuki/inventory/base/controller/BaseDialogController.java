package com.daidaisuki.inventory.base.controller;

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

  protected void InitializeBaseDialogController() {
    this.confirmButton.disableProperty().bind(this.viewModel.isInvalidProperty());
  }

  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }

  public boolean isConfirmed() {
    return this.confirmed;
  }

  public R getResult() {
    return this.confirmed ? this.viewModel.createResult() : null;
  }

  @FXML
  protected void handleConfirm() {
    if (this.viewModel.isInvalidProperty().get()) {
      return;
    }
    this.confirmed = true;
    this.dialogStage.close();
  }

  @FXML
  protected void handleCancel() {
    this.dialogStage.close();
  }
}
