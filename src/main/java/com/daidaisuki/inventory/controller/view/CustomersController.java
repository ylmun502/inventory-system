package com.daidaisuki.inventory.controller.view;


/* Comment out during mvvm migration as need to refactor view by view

public class CustomersController extends BaseTableController<Customer, CustomersViewModel> {
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

  public CustomersController(CustomersViewModel viewModel) {
    super(viewModel);
  }

  @FXML
  public void initialize() {
    setupColumns();
    setupOrderColumn();
    initializeBase(this.table, this.addButton, this.editButton, this.deleteButton);
    this.fullNameCol.setSortType(TableColumn.SortType.ASCENDING);
    this.table.getSortOrder().add(this.fullNameCol);
    this.table.sort();
    this.orderDateCol.setSortType(TableColumn.SortType.DESCENDING);
    this.orderTable.getSortOrder().add(this.orderDateCol);
    this.table
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              updateDetailPane(newVal);
            });
  }

  private void setupColumns() {
    List<Double> ratios = new ArrayList<>(Collections.nCopies(7, 0.2));
    TableColumnUtils.bindColumnWidthsByRatio(this.table, ratios);
    this.fullNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
    this.acquisitionSourceCol.setCellValueFactory(
        cellData -> cellData.getValue().acquisitionSourceProperty());
    this.totalSpentCol.setCellValueFactory(cellData -> cellData.getValue().totalSpentProperty());
    this.lastOrderDateCol.setCellValueFactory(
        cellData -> cellData.getValue().lastOrderDateProperty());

    this.fullNameCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
    this.acquisitionSourceCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
    this.totalSpentCol.setCellFactory(TableCellUtils.centerAlignedCurrencyCellFactory());
    this.lastOrderDateCol.setCellFactory(TableCellUtils.centerAlignedDateCellFactory());
  }

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

  private void updateDetailPane(Customer customer) {
    unBindLabels();
    if (customer == null) {
      clearLabels();
      this.orderTable.setItems(FXCollections.emptyObservableList());
      return;
    }
    bindLabels(customer);
  }

  private void bindLabels(Customer customer) {
    this.fullNameLabel.textProperty().bind(customer.fullNameProperty());
    this.acquisitionSourceLabel.textProperty().bind(customer.acquisitionSourceProperty());
    this.emailLabel.textProperty().bind(customer.emailProperty());
    this.phoneNumberLabel.textProperty().bind(customer.phoneNumberProperty());
    this.addressLabel.textProperty().bind(customer.addressProperty());
    this.totalOrdersLabel.textProperty().bind(customer.totalOrdersProperty().asString());
    this.totalSpentLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> CurrencyUtil.format(customer.getTotalSpent()),
                customer.totalSpentProperty()));
    this.totalDiscountLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> CurrencyUtil.format(customer.getTotalDiscount()),
                customer.totalDiscountProperty()));
    this.averageOrderValueLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> CurrencyUtil.format(customer.getAverageOrderValue()),
                customer.averageOrderValueProperty()));
    this.createdAtLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> DateUtils.format(customer.getCreatedAt()), customer.createdAtProperty()));
    this.updatedAtLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> DateUtils.format(customer.getUpdatedAt()), customer.updatedAtProperty()));
    this.orderTable.setItems(this.viewModel.getOrdersForCustomer(customer.getId()));
  }

  private void unBindLabels() {
    fullNameLabel.textProperty().unbind();
    acquisitionSourceLabel.textProperty().unbind();
    emailLabel.textProperty().unbind();
    phoneNumberLabel.textProperty().unbind();
    addressLabel.textProperty().unbind();
    totalOrdersLabel.textProperty().unbind();
    totalSpentLabel.textProperty().unbind();
    totalDiscountLabel.textProperty().unbind();
    averageOrderValueLabel.textProperty().unbind();
    createdAtLabel.textProperty().unbind();
    updatedAtLabel.textProperty().unbind();
  }

  private void clearLabels() {
    fullNameLabel.setText("Select a Customer");
    acquisitionSourceLabel.setText("Source: --");
    emailLabel.setText("Email: --");
    phoneNumberLabel.setText("Phone: --");
    addressLabel.setText("Address: --");
    totalOrdersLabel.setText("0");
    totalSpentLabel.setText("$0.00");
    totalDiscountLabel.setText("$0.00");
    averageOrderValueLabel.setText("$0.00");
    createdAtLabel.setText("Created: --");
    updatedAtLabel.setText("Updated: --");
  }

  @FXML
  private void handleAdd() throws Exception {
    Customer customer =
        this.showGenericDialog(
            CustomerDialogController.class,
            DialogView.CUSTOMER_DIALOG,
            new CustomerDialogViewModel(this.viewModel.getCustomerService()),
            null);
    if (customer != null) {
      this.viewModel.add(customer);
    }
  }

  @FXML
  private void handleEdit() throws Exception {
    Customer selectedCustomer = this.viewModel.selectedItemProperty().get();
    if (selectedCustomer != null) {
      CustomerDialogViewModel dialogViewModel =
          new CustomerDialogViewModel(this.viewModel.getCustomerService());
      Customer updatedCustomer =
          showGenericDialog(
              CustomerDialogController.class,
              DialogView.CUSTOMER_DIALOG,
              dialogViewModel,
              selectedCustomer);
      if (updatedCustomer != null) {
        this.viewModel.update(updatedCustomer);
      }
    }
  }

  @FXML
  private void handleDelete() throws Exception {
    Customer selectedCustomer = this.viewModel.selectedItemProperty().get();
    if (selectedCustomer != null) {
      this.viewModel.delete(selectedCustomer);
    }
  }
}
*/
