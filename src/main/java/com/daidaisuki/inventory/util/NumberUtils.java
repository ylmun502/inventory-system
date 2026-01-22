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
}
