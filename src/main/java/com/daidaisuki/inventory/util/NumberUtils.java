package com.daidaisuki.inventory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberUtils {
  private NumberUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String format(BigDecimal value) {
    return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toString();
  }

  public static String percentage(BigDecimal value) {
    if (value == null) {
      return "0%";
    }
    return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0
        ? value.setScale(0, RoundingMode.HALF_UP) + "%"
        : value.setScale(2, RoundingMode.HALF_UP) + "%";
  }
}
