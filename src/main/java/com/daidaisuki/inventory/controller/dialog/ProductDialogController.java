package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProductDialogController extends BaseDialogController<Product> {

  @FXML private TextField nameField;
  @FXML private TextField categoryField;
  @FXML private TextField stockField;
  @FXML private TextField priceField;
  @FXML private TextField costField;
  @FXML private TextField shippingField;

  @FXML private Label dialogTitle;

  @Override
  public void setModel(Product product) {
    this.model = product;
    boolean isEdit = product != null;
    String title = isEdit ? "Edit Product" : "Add Product";
    dialogStage.setTitle(title);
    dialogTitle.setText(title);
    if (isEdit) {
      nameField.setText(product.getName());
      categoryField.setText(product.getCategory());
      stockField.setText(String.valueOf(product.getStock()));
      priceField.setText(String.valueOf(product.getPrice()));
      costField.setText(String.valueOf(product.getCost()));
      shippingField.setText(String.valueOf(product.getShipping()));
    }
  }

  @FXML
  @Override
  protected void handleSave() {
    StringBuilder errorMessage = new StringBuilder();
    String name = sanitizeInput(nameField);
    String category = sanitizeInput(categoryField);
    String stockStr = sanitizeInput(stockField);
    String priceStr = sanitizeInput(priceField);
    String costStr = sanitizeInput(costField);
    String shippingStr = sanitizeInput(shippingField);

    isFieldEmpty(name, "Name", errorMessage);
    isFieldEmpty(category, "Category", errorMessage);
    isNumeric(stockStr, "Stock Number", errorMessage, false);
    isNumeric(priceStr, "Sale Price", errorMessage, true);
    isNumeric(costStr, "Purchase Cost", errorMessage, true);
    isNumeric(shippingStr, "Shipping Fee", errorMessage, true);

    if (errorMessage.length() > 0) {
      showError(errorMessage.toString());
      return;
    }

    if (model == null) {
      model = new Product();
    }

    model.setName(name);
    model.setCategory(category);
    model.setStock(Integer.parseInt(stockStr));
    model.setPrice(Double.parseDouble(priceStr));
    model.setCost(Double.parseDouble(costStr));
    model.setShipping(Double.parseDouble(shippingStr));
    saveClicked = true;
    dialogStage.close();
  }
}
