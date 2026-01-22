package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.App;
import com.daidaisuki.inventory.base.controller.BaseTableController;
import com.daidaisuki.inventory.controller.dialog.ProductDialogController;
import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.util.ViewLoader;
import com.daidaisuki.inventory.viewmodel.view.InventoryViewModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InventoryController extends BaseTableController<Product, InventoryViewModel> {
  @FXML private TableView<Product> productTable;
  @FXML private TableColumn<Product, String> nameCol;
  @FXML private TableColumn<Product, String> skuCol;
  @FXML private TableColumn<Product, String> categoryCol;
  @FXML private TableColumn<Product, Integer> currentStockCol;
  @FXML private TableColumn<Product, Long> sellingPriceCentsCol;
  @FXML private TableColumn<Product, Boolean> isActiveCol;
  @FXML private TextField searchField;
  @FXML private Button addButton, editButton, deleteButton;

  public InventoryController() throws SQLException {
    ProductService service = new ProductService(DatabaseManager.getConnection());
    this.viewModel = new InventoryViewModel(service);
  }

  @FXML
  public void initialize() {
    setupColumns();
    initializeBase(viewModel, productTable, addButton, editButton, deleteButton);
    searchField.textProperty().bindBidirectional(viewModel.searchFilterProperty());
    productTable.setItems(viewModel.getFilteredList());
    setupRowFactory();
  }

  private void setupColumns() {
    List<Double> ratios = new ArrayList<>(Arrays.asList(0.15, 0.15, 0.1, 0.15, 0.15, 0.15));
    TableColumnUtils.bindColumnWidthsByRatio(productTable, ratios);
    nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    skuCol.setCellValueFactory(cellData -> cellData.getValue().skuProperty());
    categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
    sellingPriceCentsCol.setCellValueFactory(
        cellData -> cellData.getValue().sellingPriceCentsProperty().asObject());
    currentStockCol.setCellValueFactory(
        cellData -> cellData.getValue().currentStockProperty().asObject());
    isActiveCol.setCellValueFactory(cellData -> cellData.getValue().isActiveProperty());
  }

  private void setupRowFactory() {
    productTable.setRowFactory(
        tv -> {
          TableRow<Product> row =
              new TableRow<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                  super.updateItem(item, empty);
                  setStyle("");
                  setTooltip(null);
                  if (item != null && !empty) {
                    if (item.getCurrentStock() <= 0) {
                      setStyle("-fx-background-color: #ffcdd2");
                    } else if (item.getCurrentStock() <= item.getReorderingLevel()) {
                      setStyle("-fx-background-color: #fff9c4");
                      setTooltip(new Tooltip("Stock is low! Please reorder."));
                    }
                  }
                }
              };
          return row;
        });
  }

  @Override
  protected Product showDialog(Product productToEdit) {
    return showProductDialog(productToEdit);
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

/* MVC Strucuture
public class InventoryController extends BaseTableController<Product> {

  @FXML private TableView<Product> productTable;
  @FXML private TableColumn<Product, String> nameCol;
  @FXML private TableColumn<Product, String> categoryCol;
  @FXML private TableColumn<Product, Integer> stockCol;
  @FXML private TableColumn<Product, Long> priceCol;
  @FXML private TableColumn<Product, String> availabilityCol;
  @FXML private TableColumn<Product, Double> costCol;
  @FXML private TableColumn<Product, Double> shippingCol;

  @FXML private Button addButton;
  @FXML private Button editButton;
  @FXML private Button deleteButton;

  private final ProductService productService;

  public InventoryController() {
    this.productService = new ProductService();
  }

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
    availabilityCol.setCellValueFactory(
        cellData -> new ReadOnlyStringWrapper(cellData.getValue().isInStock() ? "Yes" : "No"));
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
    return productService.listProducts();
  }

  @Override
  protected Window getWindow() {
    return FxWindowUtils.getWindow(productTable);
  }

  @Override
  protected void addItem(Product product) throws SQLException {
    productService.createProduct(product);
  }

  @Override
  protected void updateItem(Product product) throws SQLException {
    productService.updateProduct(product);
  }

  @Override
  protected void deleteItem(Product product) throws SQLException {
    productService.removeProduct(product.getId());
  }

  @Override
  protected Product showDialog(Product productToEdit) {
    return showProductDialog(productToEdit);
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
*/
