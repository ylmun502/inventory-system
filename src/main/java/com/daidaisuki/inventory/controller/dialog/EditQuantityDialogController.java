package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.model.OrderItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class EditQuantityDialogController extends BaseDialogController<OrderItem> {
  @FXML private TextField quantityField;
  @FXML private Label errorLabel;

  @Override
  public void setModel(OrderItem orderItem) {
    this.model = orderItem;
    quantityField.setText(String.valueOf(orderItem.getQuantity()));
    errorLabel.setText("");
    quantityField.setTextFormatter(
        new TextFormatter<>(
            change -> {
              String newText = change.getControlNewText();
              return newText.matches("\\d*") ? change : null;
            }));
  }

  @FXML
  @Override
  protected void handleSave() {
    String input = quantityField.getText().trim();
    StringBuilder errors = new StringBuilder();
    int quantity = -1;

    if (input.isEmpty()) {
      errors.append("Quantity is required.");
    } else {
      try {
        quantity = Integer.parseInt(input);
        if (quantity <= 0) {
          errors.append("Quantity must be greater than zero.\n");
          return;
        }
      } catch (NumberFormatException e) {
        errors.append("Quantity must be a valid number.\n");
      }
    }

    if (errors.length() > 0) {
      errorLabel.setText(errors.toString().trim());
      return;
    }

    model.setQuantity(Integer.parseInt(input));
    saveClicked = true;
    closeDialog();
  }

  @FXML
  protected void handleCancel() {
    saveClicked = false;
    dialogStage.close();
  }
}
