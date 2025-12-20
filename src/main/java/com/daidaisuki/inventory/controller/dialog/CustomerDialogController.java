package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.viewmodel.CustomerDialogViewModel;
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

  private CustomerDialogViewModel viewModel;

  @Override
  public void setModel(Customer customer) {
    this.viewModel = new CustomerDialogViewModel(customer);
    this.model = customer;
    boolean isEdit = customer != null;
    String title = isEdit ? "Edit Customer" : "Add Customer";
    dialogStage.setTitle(title);
    dialogTitle.setText(title);
    if (isEdit) {
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
    String name = sanitizeInput(nameField);
    String phoneNumber = sanitizeOrNull(phoneNumberField);
    String email = sanitizeOrNull(emailField);
    String address = sanitizeOrNull(addressField);
    String platform = sanitizeInput(platformField);

    String errorMessage =
        viewModel.validateAndUpdateCustomer(name, phoneNumber, email, address, platform);

    if (errorMessage.isEmpty()) {
      showError(errorMessage);
      return;
    }

    if (model == null) {
      model = new Customer();
    }

    model = viewModel.getCustomer();
    saveClicked = true;
    dialogStage.close();
  }
}
