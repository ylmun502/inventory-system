package com.daidaisuki.inventory.util;

public final class StringCleaner {
  private StringCleaner() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String cleanString(String input) {
    return input == null ? "" : input.trim();
  }

  public static String cleanKey(String input) {
    return input == null ? "" : input.trim().toLowerCase();
  }

  public static String cleanOrNull(String input) {
    if (input == null) {
      return null;
    }
    String trimmed = input.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
