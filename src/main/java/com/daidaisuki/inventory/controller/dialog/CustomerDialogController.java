package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.viewmodel.dialog.CustomerDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class CustomerDialogController
    extends BaseDialogController<Customer, CustomerDialogViewModel> {

  @FXML private TextField fullNameField;
  @FXML private TextField phoneNumberField;
  @FXML private TextField emailField;
  @FXML private TextField addressField;
  @FXML private TextField acquisitionSourceField;

  @FXML private Label dialogTitle;

  public CustomerDialogController(CustomerDialogViewModel viewModel) {
    super(viewModel);
  }

  @FXML
  public void initialize() {
    this.setupBinding();
    this.dialogTitle
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.isNewProperty())
                .then("Create Customer")
                .otherwise("Edit Customer"));
    this.confirmButton.disableProperty().bind(this.viewModel.isInvalidProperty());
  }

  private void setupBinding() {
    this.phoneNumberField.setTextFormatter(
        new TextFormatter<>(
            change -> change.getControlNewText().matches("\\+?[0-9]{0,15}") ? change : null));
    this.fullNameField.textProperty().bindBidirectional(this.viewModel.fullName);
    this.phoneNumberField.textProperty().bindBidirectional(this.viewModel.phoneNumber);
    this.emailField.textProperty().bindBidirectional(this.viewModel.email);
    this.addressField.textProperty().bindBidirectional(this.viewModel.address);
    this.acquisitionSourceField.textProperty().bindBidirectional(this.viewModel.acquisitionSource);
  }
}
