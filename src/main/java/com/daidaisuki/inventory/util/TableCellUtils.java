package com.daidaisuki.inventory.util;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
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

    /**
     * Setup the Actions column with Edit and Delete buttons for each row.
     */
    public static <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> createActionCellFactory(
        Consumer<T> onEdit, Consumer<T> onDelete) {
        return param -> new TableCell<T, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                pane.setAlignment(Pos.CENTER);

                editButton.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    onEdit.accept(item);
                });

                deleteButton.setOnAction(e -> {
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
