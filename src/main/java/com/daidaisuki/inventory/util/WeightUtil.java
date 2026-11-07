package com.daidaisuki.inventory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class WeightUtil {
  private static final int SCALE = 3;

  private WeightUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String format(BigDecimal weight) {
    if (weight == null) {
      return "0 grams";
    }
    BigDecimal stripped = weight.setScale(SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    String suffix = stripped.compareTo(BigDecimal.ONE) == 0 ? " gram" : " grams";
    return stripped.toPlainString() + suffix;
  }

  public static BigDecimal longToBigDecimal(long weight) {
    return BigDecimal.valueOf(weight).movePointLeft(SCALE);
  }

  public static long bigDecimalToLong(BigDecimal weight) {
    return weight == null
        ? 0L
        : weight.movePointRight(SCALE).setScale(0, RoundingMode.HALF_UP).longValueExact();
  }

  public static String formatForInput(BigDecimal weight) {
    if (weight == null) {
      return "0";
    }
    return weight.stripTrailingZeros().toPlainString();
  }
}
