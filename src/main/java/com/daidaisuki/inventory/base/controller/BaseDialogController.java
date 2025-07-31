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

    protected boolean isFieldEmpty(String text, String fieldName, StringBuilder errorMessage) {
        if(text.isEmpty()) {
            errorMessage.append(fieldName).append(" is required.\n");
            return true;
        }
        return false;
    }

    protected boolean isNumeric(String text, String fieldName, StringBuilder errorMessage, boolean allowDecimal) {
        String pattern = allowDecimal ? "\\d+(\\.\\d+)?" : "\\d+";
        if(text.isEmpty() || !text.matches(pattern)) {
            errorMessage.append(fieldName)
                        .append(" must be a ")
                        .append(allowDecimal ? "number" : "positive integer")
                        .append(".\n");
            return false;
        }
        return true;
    }

    protected String sanitizeInput(TextField field) {
        return field.getText() == null ? null : field.getText().trim();
    }

    protected String sanitizeOrNull(TextField field) {
        String value = sanitizeInput(field);
        return value.isEmpty() ? null : value;
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
