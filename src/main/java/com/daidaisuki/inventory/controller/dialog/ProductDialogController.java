package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.viewmodel.dialog.ProductDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProductDialogController extends BaseDialogController<Product> {
  @FXML private TextField skuField;
  @FXML private TextField nameField;
  @FXML private TextField categoryField;
  @FXML private TextField descriptionField;
  @FXML private TextField weightField;
  @FXML private TextField stockField;
  @FXML private TextField priceField;

  @FXML private CheckBox activeCheckBox;

  @FXML private Label dialogTitle;

  private ProductDialogViewModel viewModel;

  public void setViewModel(ProductDialogViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public Product getModel() {
    return this.viewModel.getModel();
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
  protected void handleSave() {
    if (!this.viewModel.validate()) {
      showError(this.viewModel.getValidationsErrors());
      return;
    }

    try {
      this.viewModel.save();
      this.saveClicked = true;
      closeDialog();
    } catch (Exception e) {
      showError("Failed to save product: \n" + e.getMessage());
    }
  }
}
