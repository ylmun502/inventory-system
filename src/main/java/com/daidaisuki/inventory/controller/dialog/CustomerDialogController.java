package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Customer;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerDialogController extends BaseDialogController<Customer> {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");
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
    StringBuilder errorMessage = new StringBuilder();
    String name = sanitizeInput(nameField);
    String phoneNumber = sanitizeOrNull(phoneNumberField);
    String email = sanitizeOrNull(emailField);
    String address = sanitizeOrNull(addressField);
    String platform = sanitizeInput(platformField);

    isFieldEmpty(name, "Name", errorMessage);
    isPhoneNumberValid(phoneNumber, errorMessage);
    isEmailValid(email, errorMessage);
    isFieldEmpty(platform, "Platform", errorMessage);

    if (errorMessage.length() > 0) {
      showError(errorMessage.toString());
      return;
    }

    if (model == null) {
      model = new Customer();
    }

    model.setName(name);
    model.setPhoneNumber(phoneNumber);
    model.setEmail(email);
    model.setAddress(address);
    model.setPlatform(platform);
    saveClicked = true;
    dialogStage.close();
  }

  public boolean isEmailValid(String email, StringBuilder errorMessage) {
    if (email == null) {
      return true;
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      errorMessage.append("Email format is invalid.\n");
      return false;
    }
    return true;
  }

  public boolean isPhoneNumberValid(String phoneNumber, StringBuilder errorMessage) {
    if (phoneNumber == null) {
      return true;
    }
    if (!PHONENUMBER_PATTERN.matcher(phoneNumber).matches()) {
      errorMessage.append("Phone number format is invalid.\n");
      return false;
    }
    return true;
  }
}
