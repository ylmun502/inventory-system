package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.App;
import com.daidaisuki.inventory.base.controller.BaseTableController;
import com.daidaisuki.inventory.controller.dialog.ProductDialogController;
import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.util.ViewLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public class InventoryController extends BaseTableController<Product> {
     
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

    @FXML
    public void initialize() {
        setupColumns();
        initializeBase(productTable, addButton, editButton, deleteButton);

        // Set default sort on nameCol ascending
        nameCol.setSortType(TableColumn.SortType.ASCENDING);
        productTable.getSortOrder().add(nameCol);
    }

    private void setupColumns() {
        List<Double> ratios = new ArrayList<>(Arrays.asList(0.15, 0.15, 0.1, 0.15, 0.15, 0.15, 0.15));
        TableColumnUtils.bindColumnWidthsByRatio(productTable, ratios);
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

    @Override
    protected List<Product> fetchFromDB() throws SQLException {
        return productDAO.getAllProducts();
    }

    @Override
    protected Window getWindow() {
        return FxWindowUtils.getWindow(productTable);
    }

    @Override
    protected void addItem(Product item) throws SQLException {
        productDAO.addProduct(item);
    }

    @Override
    protected void updateItem(Product item) throws SQLException {
        productDAO.updateProduct(item);
    }

    @Override
    protected void deleteItem(Product item) throws SQLException {
        productDAO.deleteProduct(item.getId());
    }

    @Override
    protected Product showDialog(Product itemToEdit) {
        return showProductDialog(itemToEdit);
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
}
