package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseTableController;
import com.daidaisuki.inventory.controller.dialog.ReceiveStockDialogController;
import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.service.InventoryService;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.service.SupplierService;
import com.daidaisuki.inventory.user.AppSession;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.CurrencyUtil;
import com.daidaisuki.inventory.util.NumberUtils;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.viewmodel.dialog.ReceiveStockDialogViewModel;
import com.daidaisuki.inventory.viewmodel.view.InventoryViewModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class InventoryController extends BaseTableController<Product, InventoryViewModel> {
  @FXML private TableView<Product> productTable;
  @FXML private TableColumn<Product, String> skuCol;
  @FXML private TableColumn<Product, String> nameCol;
  @FXML private TableColumn<Product, String> categoryCol;
  @FXML private TableColumn<Product, Number> currentStockCol;
  @FXML private TableColumn<Product, BigDecimal> sellingPriceCol;
  @FXML private TableColumn<Product, String> statusCol;
  @FXML private TextField searchField;
  @FXML private Button addButton;
  @FXML private Button editButton;
  @FXML private Button deleteButton;
  @FXML private Button receiveStockButton;

  @FXML private TableView<StockBatch> batchesTable;
  @FXML private TableColumn<StockBatch, Number> batchIdCol;
  @FXML private TableColumn<StockBatch, String> batchCode;
  @FXML private TableColumn<StockBatch, OffsetDateTime> batchDateCol;
  @FXML private TableColumn<StockBatch, OffsetDateTime> batchExpiryCol;
  @FXML private TableColumn<StockBatch, Number> quantityRemainingCol;
  @FXML private TableColumn<StockBatch, BigDecimal> unitCostCol;
  @FXML private TableColumn<StockBatch, BigDecimal> landedCostCol;

  @FXML private TableView<InventoryTransaction> transactionTable;
  @FXML private TableColumn<InventoryTransaction, OffsetDateTime> transactionDateCol;
  @FXML private TableColumn<InventoryTransaction, String> transactionTypeCol;
  @FXML private TableColumn<InventoryTransaction, Number> transactionAmountCol;
  @FXML private TableColumn<InventoryTransaction, String> transactionReasonCol;

  @FXML private Label userLabel;
  @FXML private Label barcodeLabel;
  @FXML private Label reorderLevelLabel;
  @FXML private Label taxLabel;
  @FXML private Label weightLabel;
  @FXML private Label unitTypeLabel;
  @FXML private Label minStockLabel;
  @FXML private Label averageUnitCostLabel;
  @FXML private Label markupLabel;
  @FXML private Label productTotalValueLabel;

  public InventoryController() throws SQLException {
    this(DatabaseManager.getConnection());
  }

  private InventoryController(Connection connection) throws SQLException {
    super(
        new InventoryViewModel(
            new ProductService(connection),
            new InventoryService(connection),
            new SupplierService(connection)));
  }

  @FXML
  public void initialize() {
    this.userLabel.setText("User: " + AppSession.getInstance().getUserName());
    this.setupMainTableColumns();
    this.setupDetailTablesColumns();
    this.initializeBase(productTable, addButton, editButton, deleteButton);
    this.searchField.textProperty().bindBidirectional(viewModel.searchFilterProperty());
    this.productTable.setItems(this.viewModel.getFilteredList());
    this.batchesTable.setItems(this.viewModel.getSelectedProductBatches());
    this.transactionTable.setItems(this.viewModel.getSelectedProductTransactions());
    this.setupRowFactory();
    this.table
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              this.viewModel.setSelectedItem(newVal);
            });
    this.viewModel
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              updateDetailPanel(newVal);
            });
  }

  private void setupMainTableColumns() {
    nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    skuCol.setCellValueFactory(cellData -> cellData.getValue().skuProperty());
    categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
    currentStockCol.setCellValueFactory(cellData -> cellData.getValue().currentStockProperty());
    sellingPriceCol.setCellValueFactory(cellData -> cellData.getValue().sellingPriceProperty());
    statusCol.setCellValueFactory(cellData -> cellData.getValue().stockStatusProperty());

    TableCellUtils.setupStringCells(nameCol, skuCol, categoryCol, statusCol);
    TableCellUtils.setupNumberCells(currentStockCol);
    TableCellUtils.setupCurrencyCells(sellingPriceCol);

    TableColumnUtils.bindColumnWidthsByRatio(
        productTable, List.of(0.15, 0.15, 0.15, 0.2, 0.2, 0.15));
  }

  private void setupDetailTablesColumns() {
    setupBatchesTable();
    setupTransactionsTable();
  }

  private void setupBatchesTable() {
    batchIdCol.setCellValueFactory(celldata -> celldata.getValue().idProperty());
    batchDateCol.setCellValueFactory(celldata -> celldata.getValue().createdAtProperty());
    batchExpiryCol.setCellValueFactory(celldata -> celldata.getValue().expiryDateProperty());
    quantityRemainingCol.setCellValueFactory(
        celldata -> celldata.getValue().quantityRemainingProperty());
    unitCostCol.setCellValueFactory(celldata -> celldata.getValue().unitCostProperty());
    landedCostCol.setCellValueFactory(celldata -> celldata.getValue().landedCostProperty());

    TableCellUtils.setupNumberCells(batchIdCol, quantityRemainingCol);
    TableCellUtils.setupCurrencyCells(unitCostCol, landedCostCol);
    TableCellUtils.setupDateCells(batchDateCol, batchExpiryCol);

    TableColumnUtils.bindColumnWidthsByRatio(batchesTable, List.of(0.1, 0.2, 0.2, 0.2, 0.15, 0.15));
  }

  private void setupTransactionsTable() {
    transactionDateCol.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
    transactionTypeCol.setCellValueFactory(
        cellData ->
            Bindings.createStringBinding(
                () -> cellData.getValue().getTransactionType().getDisplayName(),
                cellData.getValue().transactionTypeProperty()));
    transactionAmountCol.setCellValueFactory(
        cellData -> cellData.getValue().changeAmountProperty());
    transactionReasonCol.setCellValueFactory(cellData -> cellData.getValue().reasonCodeProperty());

    TableCellUtils.setupStringCells(transactionTypeCol, transactionReasonCol);
    TableCellUtils.setupNumberCells(transactionAmountCol);
    TableCellUtils.setupDateCells(transactionDateCol);

    TableColumnUtils.bindColumnWidthsByRatio(transactionTable, List.of(0.25, 0.25, 0.25, 0.25));
  }

  /*  Will remove after implementing bindings in ViewModel
  private void updateDetailPanel(Product product) {
    unBindLabels();
    if (product == null) {
      clearLabel();
      return;
    }
    bindLabels(product);
  }
  */

  private void bindLabels(Product product) {
    this.barcodeLabel.textProperty().bind(product.barcodeProperty());
    this.reorderLevelLabel.textProperty().bind(product.reorderingLevelProperty().asString());
    this.taxLabel.textProperty().bind(product.taxCategoryProperty());
    this.weightLabel.textProperty().bind(product.weightProperty().asString());
    this.unitTypeLabel.textProperty().bind(product.unitTypeProperty());
    this.minStockLabel.textProperty().bind(product.minStockLevelProperty().asString());
    this.averageUnitCostLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> CurrencyUtil.format(product.getAverageUnitCost()),
                product.averageUnitCostProperty()));
    this.markupLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  BigDecimal cost = product.getAverageUnitCost();
                  BigDecimal price = product.getSellingPrice();
                  if (cost.compareTo(BigDecimal.ZERO) == 0) {
                    return "0%";
                  }
                  BigDecimal markup =
                      price
                          .subtract(cost)
                          .divide(cost, 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100));
                  return NumberUtils.percentage(markup);
                },
                product.averageUnitCostProperty(),
                product.sellingPriceProperty()));
    this.productTotalValueLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  BigDecimal totalValue =
                      product
                          .getAverageUnitCost()
                          .multiply(BigDecimal.valueOf(product.getCurrentStock()));
                  return CurrencyUtil.format(totalValue);
                },
                product.averageUnitCostProperty(),
                product.currentStockProperty()));
  }

  private void unBindLabels() {
    this.barcodeLabel.textProperty().unbind();
    this.reorderLevelLabel.textProperty().unbind();
    this.taxLabel.textProperty().unbind();
    this.weightLabel.textProperty().unbind();
    this.unitTypeLabel.textProperty().unbind();
    this.minStockLabel.textProperty().unbind();
    this.averageUnitCostLabel.textProperty().unbind();
    this.markupLabel.textProperty().unbind();
    this.productTotalValueLabel.textProperty().unbind();
  }

  private void setupRowFactory() {
    productTable.setRowFactory(
        tv ->
            new TableRow<>() {
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
            });
  }

  @FXML
  protected void handleAdd() throws Exception {
    /* fix after ui representation is completed
    Product product =
        this.showDialog(
            ProductDialogController.class,
            DialogView.PRODUCT_DIALOG,
            new ProductDialogViewModel(this.viewModel.getProductService(), null));
    if (product != null) {
      this.viewModel.add(product);
    }
      */
  }

  @FXML
  protected void handleEdit() throws Exception {
    /* fix after ui represenation is completed
    Product selecteProduct = this.viewModel.selectedItemProperty().get();
    if (selecteProduct != null) {
      ProductDialogViewModel dialogViewModel =
          new ProductDialogViewModel(this.viewModel.getProductService(), selecteProduct);
      Product updatedProduct =
          this.showDialog(
              ProductDialogController.class, DialogView.PRODUCT_DIALOG, dialogViewModel);
      if (updatedProduct != null) {
        this.viewModel.update(updatedProduct);
      }
    }
      */
  }

  @FXML
  protected void handleDelete() throws Exception {
    Product selectedProduct = this.viewModel.selectedItemProperty().get();
    ;
    if (selectedProduct != null) {
      this.viewModel.delete(selectedProduct);
    }
  }

  @FXML
  private void handleReceiveStock() {
    Product selected = this.viewModel.selectedItemProperty().get();
    if (selected == null) {
      return;
    }
    try {
      List<Supplier> suppliers = this.viewModel.getSupplierService().listAll();
      ReceiveStockDialogViewModel dialogViewModel =
          new ReceiveStockDialogViewModel(selected, suppliers);
      StockReceiveRequest request =
          showDialog(
              ReceiveStockDialogController.class, DialogView.RECEIVE_STOCK_DIALOG, dialogViewModel);
      if (request != null) {
        this.viewModel.receiveStock(
            dialogViewModel.createResult(), AppSession.getInstance().getUserId());
      }
    } catch (SQLException e) {
      AlertHelper.showDatabaseError(getWindow(), "Failed to load suppliers", e);
    }
  }

  @FXML
  private void handleAdjustStock(ActionEvent event) {}

  @FXML
  private void handleProcessReturn(ActionEvent event) {}
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
