package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.base.controller.BaseCrudController;
import com.daidaisuki.inventory.controller.dialog.CustomerDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.enums.FulfillmentStatus;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.viewmodel.dialog.CustomerDialogViewModel;
import com.daidaisuki.inventory.viewmodel.view.CustomersViewModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CustomersController extends BaseCrudController<Customer, CustomersViewModel> {
  @FXML private TableColumn<Customer, String> fullNameCol;
  @FXML private TableColumn<Customer, BigDecimal> totalSpentCol;
  @FXML private TableColumn<Customer, String> acquisitionSourceCol;
  @FXML private TableColumn<Customer, OffsetDateTime> lastOrderDateCol;

  @FXML private Label fullNameLabel;
  @FXML private Label emailLabel;
  @FXML private Label phoneNumberLabel;
  @FXML private Label addressLabel;
  @FXML private Label totalOrdersLabel;
  @FXML private Label totalSpentLabel;
  @FXML private Label totalDiscountLabel;
  @FXML private Label averageOrderValueLabel;
  @FXML private Label acquisitionSourceLabel;
  @FXML private Label createdAtLabel;
  @FXML private Label updatedAtLabel;

  @FXML private TableView<Order> orderTable;
  @FXML private TableColumn<Order, Number> orderIdCol;
  @FXML private TableColumn<Order, OffsetDateTime> orderDateCol;
  @FXML private TableColumn<Order, FulfillmentStatus> orderStatusCol;
  @FXML private TableColumn<Order, BigDecimal> orderTotalCol;

  public CustomersController(ServiceRegistry registry) throws SQLException {
    super(new CustomersViewModel(registry.getCustomerService(), registry.getOrderService()));
  }

  @FXML
  public void initialize() {
    this.setupStaticUI();
    this.setupEventShortcuts();
    // setupOrderColumn();
    this.initializeBaseCrudController();
  }

  @Override
  protected void bindViewModelProperties() {
    super.bindViewModelProperties();
    this.bindButtons();
    this.bindLabels();
  }

  @Override
  protected void setupTableDataBinding() {
    super.setupTableDataBinding();
    this.orderTable.setItems(this.viewModel.getSelectedCustomerOrders());
  }

  @Override
  protected Customer showEntityDialog(Customer customer) {
    CustomerDialogViewModel dialogViewModel = new CustomerDialogViewModel(customer);
    return this.getDialogService()
        .showDialog(CustomerDialogController.class, DialogView.CUSTOMER_DIALOG, dialogViewModel);
  }

  @Override
  protected String getArchiveConfirmationMessage(Customer customer) {
    return "Are you sure you want to archive " + customer.getFullName() + "?";
  }

  @Override
  protected String getRestoreConfirmationMessage(Customer customer) {
    return "Are you sure you want to restore " + customer.getFullName() + "?";
  }

  @Override
  protected String getPurgeConfirmationMessage(Customer customer) {
    return "Are you sure you want to permanently delete " + customer.getFullName() + "?";
  }

  private void setupStaticUI() {
    this.setupMainTableColumns();
  }

  private void setupEventShortcuts() {
    this.setupDeselectOnEmptySpace(orderTable);
  }

  private void bindLabels() {
    this.fullNameLabel.textProperty().bind(this.viewModel.fullNameTextProperty());
    this.emailLabel.textProperty().bind(this.viewModel.emailTextProperty());
    this.phoneNumberLabel.textProperty().bind(this.viewModel.phoneNumberTextProperty());
    this.addressLabel.textProperty().bind(this.viewModel.addressTextProperty());
    this.totalOrdersLabel.textProperty().bind(this.viewModel.totalOrderTextProperty());
    this.totalSpentLabel.textProperty().bind(this.viewModel.totalSpentTextProperty());
    this.totalDiscountLabel.textProperty().bind(this.viewModel.totalDiscountTextProperty());
    this.averageOrderValueLabel.textProperty().bind(this.viewModel.averageOrderValueTextProperty());
    this.acquisitionSourceLabel.textProperty().bind(this.viewModel.acquisitionSourceTextProperty());
    this.createdAtLabel.textProperty().bind(this.viewModel.createdAtTextProperty());
    this.updatedAtLabel.textProperty().bind(this.viewModel.updatedAtTextProperty());
  }

  private void bindButtons() {
    this.archiveButton
        .textProperty()
        .bind(
            Bindings.when(this.viewModel.showArchivedProperty())
                .then("Purge")
                .otherwise("Archive"));
  }

  private void setupMainTableColumns() {
    this.fullNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
    this.totalSpentCol.setCellValueFactory(cellData -> cellData.getValue().totalSpentProperty());
    this.acquisitionSourceCol.setCellValueFactory(
        cellData -> cellData.getValue().acquisitionSourceProperty());
    this.lastOrderDateCol.setCellValueFactory(
        cellData -> cellData.getValue().lastOrderDateProperty());
    TableCellUtils.setupStringCells(this.fullNameCol, this.acquisitionSourceCol);
    TableCellUtils.setupCurrencyCells(this.totalSpentCol);
    TableCellUtils.setupDateCells(this.lastOrderDateCol);
    TableColumnUtils.bindColumnWidthsByRatio(this.table, List.of(0.3, 0.2, 0.3, 0.2));
  }

  /*
  private void setupOrderColumn() {
    this.orderIdCol.setCellValueFactory(celldata -> celldata.getValue().idProperty());
    this.orderDateCol.setCellValueFactory(celldata -> celldata.getValue().createdAtProperty());
    this.orderStatusCol.setCellValueFactory(
        celldata -> celldata.getValue().fulfillmentStatusProperty());
    this.orderTotalCol.setCellValueFactory(celldata -> celldata.getValue().totalItemsProperty());

    this.orderIdCol.setCellFactory(TableCellUtils.centerAlignedNumberCellFactory());
    this.orderDateCol.setCellFactory(TableCellUtils.centerAlignedDateCellFactory());
    this.orderStatusCol.setCellFactory(TableCellUtils.centerAlignedEnumCellFactory());
    this.orderTotalCol.setCellFactory(TableCellUtils.centerAlignedCurrencyCellFactory());
  }
  */
}
