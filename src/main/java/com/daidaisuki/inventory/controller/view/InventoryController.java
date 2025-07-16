package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.App;
import com.daidaisuki.inventory.controller.dialog.ProductDialogController;
import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxUiUtils;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.ViewLoader;
import com.daidaisuki.inventory.util.FxWindowUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;

public class InventoryController {
     
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, String> categoryCol;
    @FXML private TableColumn<Product, Integer> stockCol;
    @FXML private TableColumn<Product, Double> priceCol;
    @FXML private TableColumn<Product, String> availabilityCol;
    @FXML private TableColumn<Product, Double> costCol;
    @FXML private TableColumn<Product, Double> shippingCol;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        productTable.setItems(productList);

        // Set default sort on nameCol ascending
        nameCol.setSortType(TableColumn.SortType.ASCENDING);
        productTable.getSortOrder().add(nameCol);
        

        try {
            // Populate list from DB and perform initial sort
            refreshTable();
        } catch (SQLException e) {
            AlertHelper.showDatabaseError(getWindow(), "Unable to load products from database.", e);
        }

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editButton.setDisable(!selected);
            deleteButton.setDisable(!selected);
        });

        boolean hasSelection = productTable.getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    private void setupColumns() {
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        stockCol.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        availabilityCol.setCellValueFactory(cellData -> 
            new ReadOnlyStringWrapper(cellData.getValue().isInStock() ? "Yes" : "No")
        );
        costCol.setCellValueFactory(cellData -> cellData.getValue().costProperty().asObject());
        shippingCol.setCellValueFactory(cellData -> cellData.getValue().shippingProperty().asObject());

        nameCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
        stockCol.setCellFactory(TableCellUtils.centerAlignedIntegerCellFactory());
        priceCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
        categoryCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
        availabilityCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
        costCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
        shippingCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
    }

    @FXML
    private void handleAddProduct() {
        Product newProduct = showProductDialog(null);
        if(newProduct != null) {
            FxUiUtils.runWithButtonsDisabled(() -> {
                try {
                    productDAO.addProduct(newProduct);
                    refreshTable();
                } catch(SQLException e) {
                    AlertHelper.showDatabaseError(getWindow(), "Could not add product", e);
                }
            }, addButton, editButton, deleteButton);
        }
    }

    @FXML
    private void handleEditProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            AlertHelper.showSelectionRequiredAlert(getWindow(), "edit");
            return;
        }

        Product editedProduct = showProductDialog(selected);
        if(editedProduct != null) {
            FxUiUtils.runWithButtonsDisabled(() -> {
                try {
                    // Replace the product in the list
                    productDAO.updateProduct(editedProduct);
                    refreshTable();
                } catch(SQLException e) {
                    AlertHelper.showDatabaseError(getWindow(), "Could not edit product", e);
                }
            }, addButton, editButton, deleteButton);
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showSelectionRequiredAlert(getWindow(), "delete");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmationAlert(
            getWindow(),
            "Delete Product",
            null,
            "Are you sure you want to delete " + selected.getName() + "?");
        if(confirmed) {
            FxUiUtils.runWithButtonsDisabled(() -> {
                try {
                    productDAO.deleteProduct(selected.getId());
                    refreshTable();
                } catch(SQLException e) {
                    AlertHelper.showDatabaseError(getWindow(), "Could not delete product", e);
                }
            }, addButton, editButton, deleteButton);
        }
    }

    private Product showProductDialog(Product productToEdit) {
        try {
            FXMLLoader loader = ViewLoader.loadFxml(DialogView.PRODUCT_DIALOG);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(productTable.getScene().getWindow());
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            dialogStage.setScene(scene);

            ProductDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setModel(productToEdit);

            dialogStage.showAndWait();

            return controller.isSaveClicked() ? controller.getModel() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Window getWindow() {
        return FxWindowUtils.getWindow(productTable);
    }

    private void refreshTable() throws SQLException {
        List<Product> products = productDAO.getAllProducts();
        productList.setAll(products);
        productTable.sort();
    }
}
