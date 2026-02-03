package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.viewmodel.dialog.SupplierDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SupplierDialogController
    extends BaseDialogController<Supplier, SupplierDialogViewModel> {
  @FXML private Label dialogTitle;
  @FXML private TextField nameField;
  @FXML private TextField shortCodeField;
  @FXML private TextField emailField;
  @FXML private TextField phoneField;
  @FXML private TextField addressField;

  public SupplierDialogController(SupplierDialogViewModel dialogViewModel) {
    super(dialogViewModel);
  }

  @FXML
  public void initialize() {
    this.dialogTitle
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.isNewProperty())
                .then("Add New Supplier")
                .otherwise("Edit Supplier"));
    this.nameField.textProperty().bindBidirectional(this.viewModel.name);
    this.shortCodeField.textProperty().bindBidirectional(this.viewModel.shortCode);
    this.emailField.textProperty().bindBidirectional(this.viewModel.email);
    this.phoneField.textProperty().bindBidirectional(this.viewModel.phone);
    this.addressField.textProperty().bindBidirectional(this.viewModel.address);
    this.confirmButton.disableProperty().bind(this.viewModel.isInvalidProperty());
  }
}
