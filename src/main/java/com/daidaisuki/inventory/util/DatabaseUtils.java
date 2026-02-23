package com.daidaisuki.inventory.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class DatabaseUtils {
  private DatabaseUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static BigDecimal getBigDecimalFromCents(ResultSet rs, String columnName, String context)
      throws SQLException {
    try {
      long cents = rs.getLong(columnName);
      return CurrencyUtil.longToBigDecimal(cents);
    } catch (SQLException e) {
      throw new SQLException("Failed to read " + columnName + " for " + context, e);
    }
  }

  public static OffsetDateTime getOffsetDateTime(ResultSet rs, String columnName, String context)
      throws SQLException {
    String dateTime = rs.getString(columnName);
    if (dateTime == null) {
      return null;
    }
    try {
      return OffsetDateTime.parse(dateTime);
    } catch (DateTimeParseException e) {
      throw new SQLException(
          "Invalid datetime in " + columnName + " for " + context + ": " + dateTime, e);
    }
  }
}
