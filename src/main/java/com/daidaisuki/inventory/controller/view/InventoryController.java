package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseCrudController;
import com.daidaisuki.inventory.controller.dialog.ProductDialogController;
import com.daidaisuki.inventory.controller.dialog.ReceiveStockDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import com.daidaisuki.inventory.user.AppSession;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.viewmodel.dialog.ProductDialogViewModel;
import com.daidaisuki.inventory.viewmodel.dialog.ReceiveStockDialogViewModel;
import com.daidaisuki.inventory.viewmodel.view.InventoryViewModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;

public class InventoryController extends BaseCrudController<Product, InventoryViewModel> {
  @FXML private TableColumn<Product, String> skuCol;
  @FXML private TableColumn<Product, String> nameCol;
  @FXML private TableColumn<Product, String> categoryCol;
  @FXML private TableColumn<Product, Number> currentStockCol;
  @FXML private TableColumn<Product, BigDecimal> sellingPriceCol;
  @FXML private TableColumn<Product, String> statusCol;

  @FXML private TableView<StockBatch> batchesTable;
  @FXML private TableColumn<StockBatch, Number> batchIdCol;
  @FXML private TableColumn<StockBatch, String> batchCodeCol;
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

  @FXML private Button receiveStockButton;

  @FXML private Label barcodeLabel;
  @FXML private Label reorderingLevelLabel;
  @FXML private Label taxCategoryLabel;
  @FXML private Label weightLabel;
  @FXML private Label unitTypeLabel;
  @FXML private Label minStockLevelLabel;
  @FXML private Label averageUnitCostLabel;
  @FXML private Label markupLabel;
  @FXML private Label totalValueLabel;

  public InventoryController(ServiceRegistry registry) throws SQLException {
    super(
        new InventoryViewModel(
            registry.getProductService(),
            registry.getInventoryService(),
            registry.getSupplierService()));
  }

  /*
   * ======================================
   * JavaFX Lifecycle
   * ======================================
   */

  @FXML
  public void initialize() {
    // Static UI Setup
    this.setupStaticUI();

    // Event / Shortcut Setup
    this.setupEventShortcuts();

    /* Data Assignment (Keep these last to ensure columns and shortcuts are ready before loading rows) */
    this.initializeBaseCrudController();
  }

  /*
   * ======================================
   * Framework Overrides (Template Hooks)
   * ======================================
   */

  @Override
  protected void bindViewModelProperties() {
    this.bindLabels();
  }

  @Override
  protected void setupTableDataBinding() {
    super.setupTableDataBinding();
    this.setupData();
  }

  @Override
  protected Product showEntityDialog(Product product) {
    ProductDialogViewModel dialogViewModel =
        new ProductDialogViewModel(this.viewModel.getProductService(), product);
    return this.getDialogService()
        .showDialog(ProductDialogController.class, DialogView.PRODUCT_DIALOG, dialogViewModel);
  }

  @Override
  protected String getDeleteConfirmationMessage(Product product) {
    return "Are you sure you want to delete " + product.getName() + "?";
  }

  /*
   * ======================================
   * UI Setup
   * ======================================
   */

  private void setupStaticUI() {
    this.setupMainTableColumns();
    this.setupDetailTablesColumns();
    this.setupRowFactory();
  }

  private void setupMainTableColumns() {
    this.nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    this.skuCol.setCellValueFactory(cellData -> cellData.getValue().skuProperty());
    this.categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
    this.currentStockCol.setCellValueFactory(
        cellData -> cellData.getValue().currentStockProperty());
    this.sellingPriceCol.setCellValueFactory(
        cellData -> cellData.getValue().sellingPriceProperty());
    this.statusCol.setCellValueFactory(cellData -> cellData.getValue().stockStatusProperty());

    TableCellUtils.setupStringCells(this.nameCol, this.skuCol, this.categoryCol, this.statusCol);
    TableCellUtils.setupNumberCells(this.currentStockCol);
    TableCellUtils.setupCurrencyCells(this.sellingPriceCol);

    TableColumnUtils.bindColumnWidthsByRatio(this.table, List.of(0.15, 0.15, 0.15, 0.2, 0.2, 0.15));
  }

  private void setupDetailTablesColumns() {
    setupBatchesTable();
    setupTransactionsTable();
  }

  private void setupBatchesTable() {
    this.batchIdCol.setCellValueFactory(celldata -> celldata.getValue().idProperty());
    this.batchCodeCol.setCellValueFactory(celldata -> celldata.getValue().batchCodeProperty());
    this.batchDateCol.setCellValueFactory(celldata -> celldata.getValue().createdAtProperty());
    this.batchExpiryCol.setCellValueFactory(celldata -> celldata.getValue().expiryDateProperty());
    this.quantityRemainingCol.setCellValueFactory(
        celldata -> celldata.getValue().quantityRemainingProperty());
    this.unitCostCol.setCellValueFactory(celldata -> celldata.getValue().unitCostProperty());
    this.landedCostCol.setCellValueFactory(celldata -> celldata.getValue().landedCostProperty());

    TableCellUtils.setupNumberCells(this.batchIdCol, this.quantityRemainingCol);
    TableCellUtils.setupCurrencyCells(this.unitCostCol, this.landedCostCol);
    TableCellUtils.setupDateCells(this.batchDateCol, this.batchExpiryCol);

    TableColumnUtils.bindColumnWidthsByRatio(
        this.batchesTable, List.of(0.1, 0.1, 0.15, 0.15, 0.2, 0.15, 0.15));
  }

  private void setupTransactionsTable() {
    this.transactionDateCol.setCellValueFactory(
        cellData -> cellData.getValue().createdAtProperty());
    this.transactionTypeCol.setCellValueFactory(
        cellData -> {
          return cellData.getValue().transactionTypeProperty().map(type -> type.getDisplayName());
        });
    this.transactionAmountCol.setCellValueFactory(
        cellData -> cellData.getValue().changeAmountProperty());
    this.transactionReasonCol.setCellValueFactory(
        cellData -> cellData.getValue().reasonCodeProperty());

    TableCellUtils.setupStringCells(this.transactionTypeCol, this.transactionReasonCol);
    TableCellUtils.setupNumberCells(this.transactionAmountCol);
    TableCellUtils.setupDateCells(this.transactionDateCol);

    TableColumnUtils.bindColumnWidthsByRatio(
        this.transactionTable, List.of(0.25, 0.25, 0.25, 0.25));
  }

  private void setupRowFactory() {
    this.table.setRowFactory(
        tv ->
            new TableRow<>() {
              @Override
              protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("out-of-stock", "low-stock");
                setTooltip(null);
                if (item != null && !empty) {
                  if (item.getCurrentStock() <= 0) {
                    getStyleClass().add("out-of-stock");
                  } else if (item.getCurrentStock() <= item.getReorderingLevel()) {
                    getStyleClass().add("low-stock");
                    setTooltip(new Tooltip("Stock is low! Please reorder."));
                  }
                }
              }
            });
  }

  /*
   * ======================================
   * Event & Shortcut Setup
   * ======================================
   */

  private void setupEventShortcuts() {
    this.receiveStockButton
        .disableProperty()
        .bind(this.viewModel.selectedItemProperty().isNull().or(this.viewModel.isBusyProperty()));
    this.setupDeselectOnEmptySpace(batchesTable);
    this.setupDeselectOnEmptySpace(transactionTable);
  }

  /*
   * ======================================
   * View <-> ViewModel Binding
   * ======================================
   */

  private void bindLabels() {
    this.barcodeLabel.textProperty().bind(this.viewModel.barcodeTextProperty());
    this.reorderingLevelLabel.textProperty().bind(this.viewModel.reorderingLevelTextProperty());
    this.taxCategoryLabel.textProperty().bind(this.viewModel.taxCategoryTextProperty());
    this.weightLabel.textProperty().bind(this.viewModel.weightTextProperty());
    this.unitTypeLabel.textProperty().bind(this.viewModel.unitTypeTextProperty());
    this.minStockLevelLabel.textProperty().bind(this.viewModel.minStockLevelTextProperty());
    this.averageUnitCostLabel.textProperty().bind(this.viewModel.averageUnitCostTextProperty());
    this.markupLabel.textProperty().bind(this.viewModel.markupTextProperty());
    this.totalValueLabel.textProperty().bind(this.viewModel.totalValueTextProperty());
  }

  }

  /*
   * ======================================
   * Data Wiring
   * ======================================
   */

  private void setupData() {
    this.batchesTable.setItems(this.viewModel.getSelectedProductBatches());
    this.transactionTable.setItems(this.viewModel.getSelectedProductTransactions());
  }

  /*
   * ======================================
   * Event Handlers
   * ======================================
   */

  @FXML
  private void handleReceiveStock() {
    Product selected = this.viewModel.selectedItemProperty().get();
    try {
      List<Supplier> suppliers = this.viewModel.getSupplierService().listAll();
      ReceiveStockDialogViewModel dialogViewModel =
          new ReceiveStockDialogViewModel(selected, suppliers);
      StockReceiveRequest request =
          this.getDialogService()
              .<StockReceiveRequest, ReceiveStockDialogController>showDialog(
                  ReceiveStockDialogController.class,
                  DialogView.RECEIVE_STOCK_DIALOG,
                  dialogViewModel);
      if (request != null) {
        this.viewModel.receiveStock(
            dialogViewModel.createResult(), AppSession.getInstance().getUserId());
      }
    } catch (DataAccessException e) {
      this.viewModel.handleError(e);
    }
  }

  @FXML
  private void handleAdjustStock(ActionEvent event) {}

  @FXML
  private void handleProcessReturn(ActionEvent event) {}
}
