package com.daidaisuki.inventory.util;

import com.daidaisuki.inventory.interfaces.Displayable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Utility class for creating reusable and styled {@link TableCell} factories for JavaFX {@link
 * javafx.scene.control.TableView} columns.
 *
 * <p>Includes formatting and alignment helpers for common cell types such as {@code String}, {@code
 * Integer}, and {@code BigDecimal}, with built-in support for center alignment and currency
 * formatting.
 */
public final class TableCellUtils {
  private TableCellUtils() {
    // Prevent instantiation
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn} that
   * produces center-aligned {@link javafx.scene.control.TableCell}s for {@code String} values.
   *
   * @param <T> the type of the objects contained within the TableView rows
   * @return a cell factory for center-aligned {@code String} cells
   */
  public static <T>
      Callback<TableColumn<T, String>, TableCell<T, String>> centerAlignedStringCellFactory() {
    return col ->
        new TableCell<>() {
          @Override
          protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              setGraphic(null);
            } else {
              setText(item);
              setGraphic(null);
              setAlignment(Pos.CENTER);
            }
          }
        };
  }

  /**
   * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn} that
   * produces center-aligned {@link javafx.scene.control.TableCell}s for {@code Number} values.
   *
   * @param <T> the type of the objects contained within the TableView rows
   * @return a cell factory for center-aligned {@code Number} cells
   */
  public static <T>
      Callback<TableColumn<T, Number>, TableCell<T, Number>> centerAlignedNumberCellFactory() {
    return col ->
        new TableCell<>() {
          @Override
          protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              setGraphic(null);
            } else {
              setText(item.toString());
              setGraphic(null);
              setAlignment(Pos.CENTER);
            }
          }
        };
  }

  /**
   * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn} that
   * produces center-aligned {@link javafx.scene.control.TableCell}s for {@code Number} values
   * formatted as currency in a {@link TableColumn}.
   *
   * @param <T> the type of the objects contained within the TableView rows
   * @return a cell factory for center-aligned currency-formatted {@code Number} cells
   */
  public static <T>
      Callback<TableColumn<T, BigDecimal>, TableCell<T, BigDecimal>>
          centerAlignedCurrencyCellFactory() {
    return col ->
        new TableCell<>() {
          @Override
          protected void updateItem(BigDecimal item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              setGraphic(null);
            } else {
              setText(CurrencyUtil.format(item));
              setGraphic(null);
              setAlignment(Pos.CENTER);
            }
          }
        };
  }

  /**
   * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn} that
   * produces center-aligned {@link javafx.scene.control.TableCell}s for {@code OffsetDateTime}
   * values formatted as date in a {@link TableColumn}.
   *
   * @param <T> the type of the objects contained within the TableView rows
   * @return a cell factory for center-aligned date {@code OffsetDateTime} cells
   */
  public static <T>
      Callback<TableColumn<T, OffsetDateTime>, TableCell<T, OffsetDateTime>>
          centerAlignedDateCellFactory() {
    return col ->
        new TableCell<>() {
          @Override
          protected void updateItem(OffsetDateTime item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              setGraphic(null);
            } else {
              setText(DateUtils.format(item));
              setGraphic(null);
              setAlignment(Pos.CENTER);
            }
          }
        };
  }

  /**
   * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn} that
   * produces center-aligned {@link javafx.scene.control.TableCell}s for {@code Enum} values.
   *
   * @param <T> the type of the objects contained within the TableView rows
   * @return a cell factory for center-aligned {@code Enum} cells
   */
  public static <T, E extends Enum<E> & Displayable>
      Callback<TableColumn<T, E>, TableCell<T, E>> centerAlignedEnumCellFactory() {
    return col ->
        new TableCell<>() {
          @Override
          protected void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              setGraphic(null);
            } else {
              setText(item.getDisplayName());
              setGraphic(null);
              setAlignment(Pos.CENTER);
            }
          }
        };
  }

  @SafeVarargs
  public static <T> void setupStringCells(TableColumn<T, String>... cells) {
    for (TableColumn<T, String> cell : cells) {
      cell.setCellFactory(centerAlignedStringCellFactory());
    }
  }

  @SafeVarargs
  public static <T> void setupNumberCells(TableColumn<T, Number>... cells) {
    for (TableColumn<T, Number> cell : cells) {
      cell.setCellFactory(centerAlignedNumberCellFactory());
    }
  }

  @SafeVarargs
  public static <T> void setupCurrencyCells(TableColumn<T, BigDecimal>... cells) {
    for (TableColumn<T, BigDecimal> cell : cells) {
      cell.setCellFactory(centerAlignedCurrencyCellFactory());
    }
  }

  @SafeVarargs
  public static <T> void setupDateCells(TableColumn<T, OffsetDateTime>... cells) {
    for (TableColumn<T, OffsetDateTime> cell : cells) {
      cell.setCellFactory(centerAlignedDateCellFactory());
    }
  }

  @SafeVarargs
  public static <T, E extends Enum<E> & Displayable> void setupEnumCells(
      TableColumn<T, E>... cells) {
    for (TableColumn<T, E> cell : cells) {
      cell.setCellFactory(centerAlignedEnumCellFactory());
    }
  }

  /** Setup the Actions column with Edit and Delete buttons for each row. */
  public static <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> createActionCellFactory(
      Consumer<T> onEdit, Consumer<T> onDelete) {
    return param ->
        new TableCell<T, Void>() {
          private final Button editButton = new Button("Edit");
          private final Button deleteButton = new Button("Delete");
          private final HBox pane = new HBox(5, editButton, deleteButton);

          {
            pane.setAlignment(Pos.CENTER);

            editButton.setOnAction(
                event -> {
                  T item = getTableView().getItems().get(getIndex());
                  onEdit.accept(item);
                });

            deleteButton.setOnAction(
                e -> {
                  T item = getTableView().getItems().get(getIndex());
                  onDelete.accept(item);
                });
          }

          @Override
          protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : pane);
          }
        };
  }
}
