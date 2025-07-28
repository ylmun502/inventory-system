package com.daidaisuki.inventory.base.controller;

import com.daidaisuki.inventory.util.AlertHelper;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
        AlertHelper.showErrorAlert(
            dialogStage,
            "Invalid Input",
            "Please fix input errors",
            message
        );
    }

    protected boolean isFieldEmpty(TextField field, String fieldName, StringBuilder errorMessage) {
        if(field.getText() == null || field.getText().trim().isEmpty()) {
            errorMessage.append(fieldName).append(" is required.\n");
            return true;
        }
        return false;
    }

    protected boolean isNumeric(TextField field, String fieldName, StringBuilder errorMessage, boolean allowDecimal) {
        String pattern = allowDecimal ? "\\d+(\\.\\d+)?" : "\\d+";
        if(field.getText() == null || !field.getText().matches(pattern)) {
            errorMessage.append(fieldName).append(" must be a ").append(allowDecimal ? "number" : "positive integer").append(".\n");
            return false;
        }
        return true;
    }

    protected void closeDialog() {
        if(dialogStage != null) {
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
