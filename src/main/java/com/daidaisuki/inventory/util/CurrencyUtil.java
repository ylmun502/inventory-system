package com.daidaisuki.inventory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtil {
  private static final NumberFormat CURRENCY_FORMATTER =
      NumberFormat.getCurrencyInstance(Locale.US);

  static {
    CURRENCY_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
  }

  private CurrencyUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String format(BigDecimal amount) {
    return amount == null
        ? CURRENCY_FORMATTER.format(BigDecimal.ZERO)
        : CURRENCY_FORMATTER.format(amount);
  }

  public static BigDecimal longToBigDecimal(long value) {
    return BigDecimal.valueOf(value).movePointLeft(2);
  }

  public static long bigDecimalToLong(BigDecimal value) {
    return value == null
        ? 0L
        : value.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
  }
}
