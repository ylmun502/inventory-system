package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.Displayable;

public enum FulfillmentStatus implements Displayable {
  COMPLETED("Completed"),
  PENDING("Pending"),
  SHIPPED("Shipped");

  private final String displayName;

  FulfillmentStatus(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static FulfillmentStatus fromString(String status) {
    if (status == null) {
      return PENDING;
    }
    try {
      return FulfillmentStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      return PENDING;
    }
  }
}
