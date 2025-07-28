package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Customer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerDialogController extends BaseDialogController<Customer> {

    @FXML private TextField nameField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField platformField;

    @FXML private Label dialogTitle;

    @Override
    public void setModel(Customer customer) {
        this.model = customer;
        boolean isEdit = customer != null;
        String title = isEdit ? "Edit Customer" : "Add Customer";
        dialogStage.setTitle(title);
        dialogTitle.setText(title);
        if(isEdit) {
            nameField.setText(customer.getName());
            phoneNumberField.setText(customer.getPhoneNumber());
            emailField.setText(customer.getEmail());
            addressField.setText(customer.getAddress());
            platformField.setText(customer.getPlatform());
        }
    }

    @FXML
    @Override
    protected void handleSave() {
        StringBuilder errorMessage =  new StringBuilder();

        isFieldEmpty(nameField, "Name", errorMessage);
        isFieldEmpty(platformField, "Platform", errorMessage);
        

        if(errorMessage.length() > 0) {
            showError(errorMessage.toString());
            return;
        }

        if(model == null) {
            model = new Customer();
        }

        model.setName(nameField.getText());
        model.setPhoneNumber(phoneNumberField.getText().isEmpty() ? null : phoneNumberField.getText());
        model.setEmail(emailField.getText().isEmpty() ? null : emailField.getText());
        model.setAddress(addressField.getText().isEmpty() ? null : addressField.getText());
        model.setPlatform(platformField.getText());
        saveClicked = true;
        dialogStage.close();
    }
}
