package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.viewmodel.dialog.ProductDialogViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProductDialogController extends BaseDialogController<Product, ProductDialogViewModel> {
  @FXML private TextField skuField;
  @FXML private TextField nameField;
  @FXML private TextField categoryField;
  @FXML private TextField descriptionField;
  @FXML private TextField weightField;
  @FXML private TextField priceField;

  @FXML private ComboBox<String> unitTypeComboBox;

  @FXML private CheckBox activeCheckBox;

  @FXML private Label dialogTitle;

  public ProductDialogController(ProductDialogViewModel dialogViewModel) {
    super(dialogViewModel);
  }

  @FXML
  public void initialize() {
    // Setting items first then followed by binding
    this.setupUnitTypes();
    this.setupBinding();
    this.dialogTitle
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.isNewProperty())
                .then("Create Product")
                .otherwise("Edit Product"));
    this.confirmButton.disableProperty().bind(this.viewModel.isInvalidProperty());
  }

  private void setupUnitTypes() {
    this.unitTypeComboBox.setEditable(true);
    this.unitTypeComboBox.setItems(
        FXCollections.observableArrayList(this.viewModel.getAvailableUnitTypes()));
  }

  private void setupBinding() {
    this.skuField.textProperty().bindBidirectional(this.viewModel.sku);
    this.nameField.textProperty().bindBidirectional(this.viewModel.name);
    this.categoryField.textProperty().bindBidirectional(this.viewModel.category);
    this.descriptionField.textProperty().bindBidirectional(this.viewModel.description);
    this.weightField.textProperty().bindBidirectional(this.viewModel.weight);
    this.priceField.textProperty().bindBidirectional(this.viewModel.price);
    this.unitTypeComboBox.valueProperty().bindBidirectional(this.viewModel.unitType);
    this.activeCheckBox.selectedProperty().bindBidirectional(this.viewModel.isActive);
  }
}
