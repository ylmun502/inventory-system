package com.daidaisuki.inventory.util;

import java.util.regex.Pattern;

public class ValidationUtils {
  private static final Pattern DECIMAL_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
  private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

  private ValidationUtils() {
    // Prevent instantiation
    throw new UnsupportedOperationException("Utility class");
  }

  public static boolean isFieldEmpty(String text, String fieldName, StringBuilder errorMessage) {
    if (text.isEmpty()) {
      errorMessage.append(fieldName).append(" is required.\n");
      return true;
    }
    return false;
  }

  public static boolean isNumeric(
      String text, String fieldName, StringBuilder errorMessage, boolean allowDecimal) {
    Pattern pattern = allowDecimal ? DECIMAL_PATTERN : INTEGER_PATTERN;
    if (text.isEmpty() || !pattern.matcher(text).matches()) {
      errorMessage
          .append(fieldName)
          .append(" must be a ")
          .append(allowDecimal ? "number" : "positive integer")
          .append(".\n");
      return false;
    }
    return true;
  }
}
