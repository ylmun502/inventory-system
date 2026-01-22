package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.Displayable;

public enum TransactionType implements Displayable {
  STOCK_IN("Stock In"),
  STOCK_OUT("Stock Out"),
  ADJUSTMENT("Adjustment"),
  RETURN("Return"),
  DAMAGE("Damage");

  private final String displayName;

  TransactionType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  public static TransactionType fromString(String type) {
    if (type == null) {
      return STOCK_IN;
    }
    try {
      return TransactionType.valueOf(type.toUpperCase().replace(" ", "_"));
    } catch (IllegalArgumentException e) {
      return STOCK_IN;
    }
  }
}
