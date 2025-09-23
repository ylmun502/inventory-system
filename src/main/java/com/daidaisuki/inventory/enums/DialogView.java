package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.FxmlView;

public enum DialogView implements FxmlView {
  PRODUCT_DIALOG("productDialog.fxml"),
  CUSTOMER_DIALOG("customerDialog.fxml"),
  ORDER_DIALOG("orderDialog.fxml"),
  EDIT_QUANTITY_DIALOG("editQuantityDialog.fxml");

  private final String fxml;

  DialogView(String fxml) {
    this.fxml = fxml;
  }

  @Override
  public String getFxml() {
    return "/com/daidaisuki/inventory/" + fxml;
  }
}
