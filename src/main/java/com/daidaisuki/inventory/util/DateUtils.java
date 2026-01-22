package com.daidaisuki.inventory.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtils {
  private static final DateTimeFormatter STANDARD_FORMATTER =
      DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a", Locale.getDefault());

  private DateUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String format(OffsetDateTime dateTime) {
    return dateTime == null ? "--" : dateTime.format(STANDARD_FORMATTER);
  }
}
