package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.FxmlView;

public enum View implements FxmlView {
  INVENTORY("inventory.fxml"),
  CURRENCY_CONVERTER("currencyConverter.fxml"),
  ORDERS("orders.fxml"),
  CUSTOMERS("customers.fxml"),
  REPORTS("reports.fxml"),
  SETTINGS("settings.fxml");

  private final String fxml;

  View(String fxml) {
    this.fxml = fxml;
  }

  @Override
  public String getFxml() {
    return "/com/daidaisuki/inventory/" + fxml;
  }
}
