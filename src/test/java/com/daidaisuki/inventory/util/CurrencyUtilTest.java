package com.daidaisuki.inventory.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CurrencyUtilTest {
  @Test
  void bigDecimalToLongShouldConvertAmountToCents() {
    assertEquals(1234L, CurrencyUtil.bigDecimalToLong(new BigDecimal("12.34")));
  }

  @Test
  void longToBigDecimalShouldConvertCentsToAmount() {
    assertEquals(0, CurrencyUtil.longToBigDecimal(1234L).compareTo(new BigDecimal("12.34")));
  }

  @Test
  void bigDecimalToLongShouldRoundHalfUp() {
    assertEquals(1235L, CurrencyUtil.bigDecimalToLong(new BigDecimal("12.345")));
  }

  @Test
  void bigDecimalToLongShouldReturnZeroForNull() {
    assertEquals(0L, CurrencyUtil.bigDecimalToLong(null));
  }

  @Test
  void formatForInputShouldUseTwoDecimalPlaces() {
    assertEquals("12.30", CurrencyUtil.formatForInput(new BigDecimal("12.3")));
  }

  @Test
  void formatForInputShouldReturnZeroForNull() {
    assertEquals("0.00", CurrencyUtil.formatForInput(null));
  }
}
