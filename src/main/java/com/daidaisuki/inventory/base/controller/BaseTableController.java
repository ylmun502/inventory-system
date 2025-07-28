package com.daidaisuki.inventory.base.controller;

import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;
import java.util.List;

import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxUiUtils;

import java.sql.SQLException;

public abstract class BaseTableController<T> {
    protected TableView<T> table;
    protected Button addButton, editButton, deleteButton;

    protected ObservableList<T> dataList = FXCollections.observableArrayList();
    protected abstract List<T> fetchFromDB() throws SQLException;
    protected abstract void addItem(T item) throws SQLException;
    protected abstract void updateItem(T item) throws SQLException;
    protected abstract void deleteItem(T item) throws SQLException;
    protected abstract T showDialog(T itemToEdit);
    protected abstract Window getWindow(); 

    protected void initializeBase(TableView<T> table, Button addButton, Button editButton, Button deleteButton) {
        this.table = table;
        this.addButton = addButton;
        this.editButton = editButton;
        this.deleteButton = deleteButton;

        table.setItems(dataList);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editButton.setDisable(!selected);
            deleteButton.setDisable(!selected);
        });

        editButton.setDisable(true);
        deleteButton.setDisable(true);
        
        try {
            refreshTable();
        } catch(SQLException e) {
            AlertHelper.showDatabaseError(getWindow(), "Unable to load data.", e);
        }
    }

    protected void refreshTable() throws SQLException {
        dataList.setAll(fetchFromDB());
        table.sort();
    }

    @FXML
    protected void handleAdd() {
        T newItem = showDialog(null);
        if(newItem != null) {
            FxUiUtils.runWithButtonsDisabled(() -> {
                try {
                    addItem(newItem);
                    refreshTable();
                } catch(SQLException e) {
                    AlertHelper.showDatabaseError(getWindow(), "Could not add item.", e);
                }
            }, addButton, editButton, deleteButton);
        }
    }

    @FXML
    protected void handleEdit() {
        T selected = table.getSelectionModel().getSelectedItem();
        if(selected == null) {
            AlertHelper.showSelectionRequiredAlert(getWindow(), "edit");
            return;
        }
        T edited = showDialog(selected);
        if(edited != null) {
            FxUiUtils.runWithButtonsDisabled(() -> {
                try {
                    updateItem(edited);
                    refreshTable();
                } catch(SQLException e) {
                    AlertHelper.showDatabaseError(getWindow(), "Could not edit item.", e);
                }
            }, addButton, editButton, deleteButton);
        }
    }

    @FXML
    protected void handleDelete() {
        T selected = table.getSelectionModel().getSelectedItem();
        if(selected == null) {
            AlertHelper.showSelectionRequiredAlert(getWindow(), "delete");
            return;
        }
        boolean confirmed = AlertHelper.showConfirmationAlert(
            getWindow(),
            "Delete",
            null,
            "Are you sure you want to delete this item?");
        if(confirmed) {
            FxUiUtils.runWithButtonsDisabled(() -> {
                try {
                    deleteItem(selected);
                    refreshTable();
                } catch(SQLException e) {
                    AlertHelper.showDatabaseError(getWindow(), "Could not delete item.", e);
                }
            }, addButton, editButton, deleteButton);
        }
    }
}
