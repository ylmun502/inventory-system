package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.viewmodel.dialog.ProductDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProductDialogController extends BaseDialogController<Product, ProductDialogViewModel> {
  @FXML private TextField skuField;
  @FXML private TextField nameField;
  @FXML private TextField categoryField;
  @FXML private TextField descriptionField;
  @FXML private TextField weightField;
  @FXML private TextField stockField;
  @FXML private TextField priceField;

  @FXML private CheckBox activeCheckBox;

  @FXML private Label dialogTitle;

  public ProductDialogController(ProductDialogViewModel viewModel) {
    super(viewModel);
  }

  @FXML
  public void initialize() {
    setupBinding();
    this.dialogTitle
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.isNewProperty())
                .then("Create Product")
                .otherwise("Edit Product"));
    if (confirmButton != null) {
      confirmButton.disableProperty().bind(this.viewModel.isInvalidProperty());
    }
  }

  @Override
  public Product getResult() {
    return confirmed ? this.viewModel.createResult() : null;
  }

  @Override
  public void setModel(Product product) {
    this.viewModel.setModel(product);
    setupBinding();
    this.dialogTitle
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.isNewProperty())
                .then("Create Product")
                .otherwise("Edit Product"));
    this.dialogStage
        .titleProperty()
        .bind(
            Bindings.when(this.viewModel.isNewProperty())
                .then("Add Product")
                .otherwise("Edit Product"));
  }

  private void setupBinding() {
    this.skuField.textProperty().bindBidirectional(this.viewModel.sku);
    this.nameField.textProperty().bindBidirectional(this.viewModel.name);
    this.categoryField.textProperty().bindBidirectional(this.viewModel.category);
    this.descriptionField.textProperty().bindBidirectional(this.viewModel.description);
    this.weightField.textProperty().bindBidirectional(this.viewModel.weight);
    this.stockField.textProperty().bindBidirectional(this.viewModel.stock);
    this.priceField.textProperty().bindBidirectional(this.viewModel.price);
    this.activeCheckBox.selectedProperty().bindBidirectional(this.viewModel.isActive);
  }

  @FXML
  @Override
  protected void handleConfirm() {
    if (!this.viewModel.isInvalidProperty().get()) {
      this.confirmed = true;
      this.dialogStage.close();
    } else {
    }
  }
}
