package com.daidaisuki.inventory.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class WeightUtilTest {
  @Test
  void bigDecimalToLongShouldScaleWeightByThreeDecimalPlaces() {
    assertEquals(12375L, WeightUtil.bigDecimalToLong(new BigDecimal("12.375")));
  }

  @Test
  void longToBigDecimalShouldRestoreWeight() {
    assertEquals(0, WeightUtil.longToBigDecimal(12375L).compareTo(new BigDecimal("12.375")));
  }

  @Test
  void formatForInputShouldRemoveTrailingZeros() {
    assertEquals("12.5", WeightUtil.formatForInput(new BigDecimal("12.500")));
  }
}
