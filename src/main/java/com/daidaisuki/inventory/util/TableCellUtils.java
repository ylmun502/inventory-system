package com.daidaisuki.inventory.util;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Utility class for creating reusable and styled {@link TableCell} factories
 * for JavaFX {@link javafx.scene.control.TableView} columns.
 * <p>
 * Includes formatting and alignment helpers for common cell types such as {@code String},
 * {@code Integer}, and {@code Double}, with built-in support for center alignment
 * and currency formatting.
 * </p>
 */
public class TableCellUtils {

    /**
     * Formats a numeric price into a currency string with two decimal places.
     *
     * @param price The price value to format.
     * @return A formatted string, e.g., "$12.99"
     */
    public static String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    /**
     * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn}
     * that produces center-aligned {@link javafx.scene.control.TableCell}s for {@code String} values.
     *
     * @param <T> the type of the objects contained within the TableView rows
     * @return a cell factory for center-aligned {@code String} cells
     */
    public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> centerAlignedStringCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

    /**
     * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn}
     * that produces center-aligned {@link javafx.scene.control.TableCell}s for {@code Integer} values.
     *
     * @param <T> the type of the objects contained within the TableView rows
     * @return a cell factory for center-aligned {@code Integer} cells
     */
    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> centerAlignedIntegerCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

   
    /**
     * Creates a {@link javafx.util.Callback} for a {@link javafx.scene.control.TableColumn}
     * that produces center-aligned {@link javafx.scene.control.TableCell}s for {@code Double} values formatted
     * as currency in a {@link TableColumn}.
     *
     * @param <T> the type of the objects contained within the TableView rows
     * @return a cell factory for center-aligned currency-formatted {@code Double} cells
     */
    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> centerAlignedPriceCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatPrice(item));
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }
}
