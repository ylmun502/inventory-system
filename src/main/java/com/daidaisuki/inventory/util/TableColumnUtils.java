package com.daidaisuki.inventory.util;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableColumnUtils {
  private TableColumnUtils() {
    // Prevent instantiation
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Bind TableColumn widths to TableView width based on given ratios. Ratios size list must match
   * the number of columns.
   */
  public static <T> void bindColumnWidthsByRatio(TableView<T> table, List<Double> ratios) {
    ObservableList<TableColumn<T, ?>> columns = table.getColumns();
    if (ratios.size() != columns.size()) {
      throw new IllegalArgumentException("Ratios size must match columns count");
    }
    for (int i = 0; i < columns.size(); i++) {
      TableColumn<T, ?> col = columns.get(i);
      double ratio = ratios.get(i);
      col.prefWidthProperty().bind(table.widthProperty().multiply(ratio));
    }
  }
}
