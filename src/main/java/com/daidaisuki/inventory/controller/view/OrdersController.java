package com.daidaisuki.inventory.controller.view;


/* Comment out during mvvm migration as need to refactor view by view

public class OrdersController extends BaseTableController<Order> {
  @FXML private TableView<Order> orderTable;
  @FXML private TableColumn<Order, Integer> orderIdCol;
  @FXML private TableColumn<Order, String> customerNameCol;
  @FXML private TableColumn<Order, LocalDate> dateCol;
  @FXML private TableColumn<Order, Integer> totalItemsCol;
  @FXML private TableColumn<Order, Double> totalAmountCol;
  @FXML private TableColumn<Order, Double> discountAmount;
  @FXML private TableColumn<Order, String> paymentMethodCol;

  @FXML private Button addButton;
  @FXML private Button editButton;
  @FXML private Button deleteButton;

  private final OrderService orderService;

  public OrdersController() {
    this.orderService = new OrderService();
  }

  public void initialize() {
    setupColumns();
    initializeBase(orderTable, addButton, editButton, deleteButton);
    orderIdCol.setSortType(TableColumn.SortType.ASCENDING);
    orderTable.getSortOrder().add(orderIdCol);
  }

  public void setupColumns() {
    List<Double> ratios = new ArrayList<>(Arrays.asList(0.06, 0.18, 0.1, 0.13, 0.16, 0.19, 0.18));
    TableColumnUtils.bindColumnWidthsByRatio(orderTable, ratios);
    orderIdCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
    customerNameCol.setCellValueFactory(
        cellData -> cellData.getValue().getCustomer().nameProperty());
    dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    totalItemsCol.setCellValueFactory(
        cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalItems()));
    totalAmountCol.setCellValueFactory(
        cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalAmount()));

    orderIdCol.setCellFactory(TableCellUtils.centerAlignedIntegerCellFactory());
    customerNameCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
    totalItemsCol.setCellFactory(TableCellUtils.centerAlignedIntegerCellFactory());
    totalAmountCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
    dateCol.setCellFactory(
        col ->
            new TableCell<>() {
              @Override
              protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(item.toString());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  @Override
  protected List<Order> fetchFromDB() throws SQLException {
    return orderService.listOrdersWithDetails();
  }

  @Override
  protected Window getWindow() {
    return FxWindowUtils.getWindow(orderTable);
  }

  @Override
  protected void addItem(Order order) throws SQLException {
    try {
      orderService.createOrderWithItems(order);
    } catch (InsufficientStockException e) {
      AlertHelper.showErrorAlert(
          getWindow(), "Insufficient Stock", "Cannot complete order", e.getMessage());
    }
  }

  @Override
  protected void updateItem(Order order) throws SQLException {
    try {
      orderService.updateOrder(order);
    } catch (InsufficientStockException e) {
      AlertHelper.showErrorAlert(
          getWindow(), "Insufficient Stock", "Cannot complete order", e.getMessage());
    }
  }

  @Override
  protected void deleteItem(Order order) throws SQLException {
    try {
      orderService.removeOrder(order.getId());
    } catch (InsufficientStockException e) {
      AlertHelper.showErrorAlert(
          getWindow(), "Insufficient Stock", "Cannot complete order", e.getMessage());
    }
  }

  @Override
  protected Order showDialog(Order orderToEdit) {
    return showOrderDialog(orderToEdit);
  }

  private Order showOrderDialog(Order orderToEdit) {
    try {
      FXMLLoader loader = ViewLoader.loadFxml(DialogView.ORDER_DIALOG);
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(orderTable.getScene().getWindow());
      Scene scene = new Scene(loader.load());
      scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
      dialogStage.setScene(scene);

      OrderDialogController controller = loader.getController();
      controller.setDialogStage(dialogStage);
      controller.setModel(orderToEdit);

      dialogStage.showAndWait();

      return controller.isSaveClicked() ? controller.getModel() : null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
*/
