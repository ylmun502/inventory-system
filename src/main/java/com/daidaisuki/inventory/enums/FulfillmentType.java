package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.Displayable;

public enum FulfillmentType implements Displayable {
  PICKUP("Pickup"),
  SHIPPING("Shipping");

  private final String displayName;

  FulfillmentType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static FulfillmentType fromString(String type) {
    if (type == null) {
      return SHIPPING;
    }
    try {
      return FulfillmentType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      return SHIPPING;
    }
  }
}
