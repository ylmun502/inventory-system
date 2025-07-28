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
        if(isEdit) {
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
        StringBuilder errorMessage =  new StringBuilder();

        isFieldEmpty(nameField, "Name", errorMessage);
        isFieldEmpty(categoryField, "Category", errorMessage);
        isNumeric(stockField, "Stock Number", errorMessage, false);
        isNumeric(priceField, "Sale Price", errorMessage, true);
        isNumeric(costField, "Purchase Cost", errorMessage, true);
        isNumeric(shippingField, "Shipping Fee", errorMessage, true);

        if(errorMessage.length() > 0) {
            showError(errorMessage.toString());
            return;
        }

        if(model == null) {
            model = new Product();
        }

        model.setName(nameField.getText());
        model.setCategory(categoryField.getText());
        model.setStock(Integer.parseInt(stockField.getText()));
        model.setPrice(Double.parseDouble(priceField.getText()));
        model.setCost(Double.parseDouble(costField.getText()));
        model.setShipping(Double.parseDouble(shippingField.getText()));
        saveClicked = true;
        dialogStage.close();
    }
}