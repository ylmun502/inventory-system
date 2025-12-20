package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public abstract class BaseDialogController<T> {
  protected Stage dialogStage;
  protected T model;
  protected boolean saveClicked = false;

  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }

  public boolean isSaveClicked() {
    return saveClicked;
  }

  public T getModel() {
    return model;
  }

  public abstract void setModel(T model);

  protected void showError(String message) {
    AlertHelper.showErrorAlert(dialogStage, "Invalid Input", "Please fix input errors", message);
  }

  protected String sanitizeInput(TextField field) {
    return field.getText() == null ? null : field.getText().trim();
  }

  protected String sanitizeOrNull(TextField field) {
    String value = sanitizeInput(field);
    return value.isEmpty() ? null : value;
  }

  protected void closeDialog() {
    if (dialogStage != null) {
      dialogStage.close();
    }
  }

  @FXML
  protected void handleCancel() {
    closeDialog();
  }

  @FXML
  protected abstract void handleSave();
}
