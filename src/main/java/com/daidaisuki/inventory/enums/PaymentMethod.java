package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.Displayable;

public enum PaymentMethod implements Displayable {
  CASH("Cash"),
  VENMO("Venmo"),
  ZELLE("Zelle");

  private final String displayName;

  PaymentMethod(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static PaymentMethod fromString(String method) {
    if (method == null) {
      return VENMO;
    }
    try {
      return valueOf(method.toUpperCase());
    } catch (IllegalArgumentException e) {
      return VENMO;
    }
  }
}
