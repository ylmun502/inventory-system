package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseTableController;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class InventoryController extends BaseTableController<Product, InventoryViewModel> {
  @FXML private TableColumn<Product, String> skuCol;
  @FXML private TableColumn<Product, String> nameCol;
  @FXML private TableColumn<Product, String> categoryCol;
  @FXML private TableColumn<Product, Number> currentStockCol;
  @FXML private TableColumn<Product, BigDecimal> sellingPriceCol;
  @FXML private TableColumn<Product, String> statusCol;
  @FXML private TextField searchField;
  @FXML private Button receiveStockButton;

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

  @FXML private Label userLabel;
  @FXML private Label barcodeLabel;
  @FXML private Label reorderingLevelLabel;
  @FXML private Label taxCategoryLabel;
  @FXML private Label weightLabel;
  @FXML private Label unitTypeLabel;
  @FXML private Label minStockLevelLabel;
  @FXML private Label averageUnitCostLabel;
  @FXML private Label markupLabel;
  @FXML private Label productTotalValueLabel;

  public InventoryController(ServiceRegistry registry) throws SQLException {
    super(
        new InventoryViewModel(
            registry.getProductService(),
            registry.getInventoryService(),
            registry.getSupplierService()));
  }

  @FXML
  public void initialize() {
    // Static UI Setup
    this.userLabel.setText("User: " + AppSession.getInstance().getUserName());
    this.setupMainTableColumns();
    this.setupDetailTablesColumns();
    this.setupRowFactory();

    // Event / Shortcut Setup
    this.initializeBase(); // Set up selection and table shortcuts
    this.receiveStockButton
        .disableProperty()
        .bind(this.viewModel.selectedItemProperty().isNull().or(this.viewModel.isBusyProperty()));
    this.setupEscapeHandler(
        this.searchField,
        () -> {
          if (!searchField.getText().isEmpty()) {
            searchField.clear();
          } else {
            clearSelection();
          }
        });
    this.setupDeselectOnEmptySpace(batchesTable);
    this.setupDeselectOnEmptySpace(transactionTable);
    this.setupEscapeHandler(
        this.batchesTable, () -> this.batchesTable.getSelectionModel().clearSelection());
    this.setupEscapeHandler(
        this.transactionTable, () -> this.transactionTable.getSelectionModel().clearSelection());

    // Property Bindings
    this.searchField.textProperty().bindBidirectional(this.viewModel.searchFilterProperty());
    this.viewModel.getSortedList().comparatorProperty().bind(this.table.comparatorProperty());
    this.bindLabels();

    // Data Assignment (Keep these last to ensure columns and shortcuts are ready before loading
    // rows)
    this.table.setItems(this.viewModel.getSortedList());
    this.batchesTable.setItems(this.viewModel.getSelectedProductBatches());
    this.transactionTable.setItems(this.viewModel.getSelectedProductTransactions());
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

  private void bindLabels() {
    this.barcodeLabel.textProperty().bind(this.viewModel.barcodeTextProperty());
    this.reorderingLevelLabel.textProperty().bind(this.viewModel.reorderingLevelTextProperty());
    this.taxCategoryLabel.textProperty().bind(this.viewModel.taxCategoryTextProperty());
    this.weightLabel.textProperty().bind(this.viewModel.weightTextProperty());
    this.unitTypeLabel.textProperty().bind(this.viewModel.unitTypeTextProperty());
    this.minStockLevelLabel.textProperty().bind(this.viewModel.minStockLevelTextProperty());
    this.averageUnitCostLabel.textProperty().bind(this.viewModel.averageUnitCostTextProperty());
    this.markupLabel.textProperty().bind(this.viewModel.markupTextProperty());
    this.productTotalValueLabel.textProperty().bind(this.viewModel.productTotalValueTextProperty());
  }

  private void setupRowFactory() {
    this.table.setRowFactory(
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
  private void handleAdd() {
    ProductDialogViewModel dialogViewModel =
        new ProductDialogViewModel(this.viewModel.getProductService(), null);
    Product product =
        this.getDialogService()
            .showDialog(ProductDialogController.class, DialogView.PRODUCT_DIALOG, dialogViewModel);
    if (product != null) {
      this.viewModel.add(product);
    }
  }

  @FXML
  private void handleEdit() {
    Product selected = this.viewModel.selectedItemProperty().get();
    if (selected != null) {
      ProductDialogViewModel dialogViewModel =
          new ProductDialogViewModel(this.viewModel.getProductService(), selected);
      Product updated =
          this.getDialogService()
              .showDialog(
                  ProductDialogController.class, DialogView.PRODUCT_DIALOG, dialogViewModel);
      if (updated != null) {
        this.viewModel.update(updated);
      }
    }
  }

  @Override
  protected String getDeleteConfirmationMessage(Product item) {
    return "Are you sure you want to delete " + item.getName() + "?";
  }

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
