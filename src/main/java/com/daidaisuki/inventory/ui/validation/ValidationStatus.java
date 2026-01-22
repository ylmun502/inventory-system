package com.daidaisuki.inventory.ui.validation;

public record ValidationStatus(boolean isValid, String errors) {
  public static final ValidationStatus OK = new ValidationStatus(true, "");
}
