package com.daidaisuki.inventory.controller.dialog;

import com.daidaisuki.inventory.App;
import com.daidaisuki.inventory.base.controller.BaseDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.model.OrderItem;
import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.service.CustomerService;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.ViewLoader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OrderDialogController extends BaseDialogController<Order> {

  @FXML private ComboBox<Customer> customerComboBox;
  @FXML private DatePicker datePicker;
  @FXML private TextField paymentMethodField;

  @FXML private ComboBox<Product> productComboBox;
  @FXML private TextField quantityField;
  @FXML private Button addItemButton;
  @FXML private Button saveButton;

  @FXML private TableView<OrderItem> itemTable;
  @FXML private TableColumn<OrderItem, String> productCol;
  @FXML private TableColumn<OrderItem, Integer> quantityCol;
  @FXML private TableColumn<OrderItem, Double> unitPriceCol;
  @FXML private TableColumn<OrderItem, Double> subtotalCol;
  @FXML private TableColumn<OrderItem, Void> actionCol;

  @FXML private Label dialogTitle;
  @FXML private Label totalItemsLabel;
  @FXML private Label totalAmountLabel;

  private final CustomerService customerService = new CustomerService();
  private final ProductService productService = new ProductService();
  private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
  private final Map<Integer, Product> productCache = new HashMap<>();

  @FXML
  public void initialize() {
    loadCustomers();
    loadProducts();
    productCol.setCellValueFactory(
        c -> {
          final Product product = productCache.get(c.getValue().getProductId());
          return new SimpleStringProperty(product != null ? product.getName() : "Unknown");
        });
    quantityCol.setCellValueFactory(c -> c.getValue().quantityProperty().asObject());
    unitPriceCol.setCellValueFactory(c -> c.getValue().unitPriceProperty().asObject());
    subtotalCol.setCellValueFactory(c -> c.getValue().subtotalProperty().asObject());
    quantityCol.setCellFactory(TableCellUtils.centerAlignedIntegerCellFactory());
    unitPriceCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
    subtotalCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
    actionCol.setCellFactory(
        TableCellUtils.createActionCellFactory(
            this::showEditQuantityDialog, this::deleteOrderItem));

    itemTable.setItems(orderItems);

    quantityField.setTextFormatter(
        new TextFormatter<>(
            change -> {
              String newText = change.getControlNewText();
              return newText.matches("\\d*") ? change : null;
            }));

    customerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateSaveButtonState());
    paymentMethodField.textProperty().addListener((obs, oldVal, newVal) -> updateSaveButtonState());
    datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateSaveButtonState());
    orderItems.addListener(
        (ListChangeListener<OrderItem>)
            c -> {
              updateSaveButtonState();
              updateTotals();
            });

    updateSaveButtonState();

    addItemButton.setDisable(true);
    ChangeListener<Object> itemInputListener = (obs, oldVal, newVal) -> updateAddItemButtonState();

    productComboBox.valueProperty().addListener(itemInputListener);
    quantityField.textProperty().addListener(itemInputListener);
  }

  @Override
  public void setModel(Order order) {
    this.model = order;
    boolean isEdit = order != null;
    String title = isEdit ? "Edit Order" : "Add Order";
    dialogStage.setTitle(title);
    dialogTitle.setText(title);
    if (isEdit) {
      if (!Objects.equals(customerComboBox.getValue(), order.getCustomer())) {
        customerComboBox.setValue(order.getCustomer());
      }
      datePicker.setValue(order.getDate());
      paymentMethodField.setText(order.getPaymentMethod());
      orderItems.setAll(order.getItems());
    } else {
      orderItems.clear();
    }
    updateTotals();
    updateSaveButtonState();
  }

  @FXML
  @Override
  protected void handleSave() {
    final StringBuilder errorMessage = new StringBuilder();

    if (customerComboBox.getValue() == null) {
      errorMessage.append("Customer is required.\n");
    }
    if (paymentMethodField.getText() == null || paymentMethodField.getText().trim().isEmpty()) {
      errorMessage.append("Payment Method is required.\n");
    }
    if (datePicker.getValue() == null) {
      errorMessage.append("Date is required.\n");
    }
    if (orderItems.isEmpty()) {
      errorMessage.append("At least one order item is required.\n");
    }

    if (errorMessage.length() > 0) {
      showError(errorMessage.toString());
      return;
    }

    if (model == null) {
      model = new Order();
    }

    model.setCustomer(customerComboBox.getValue());
    model.setDate(datePicker.getValue());
    model.setPaymentMethod(paymentMethodField.getText());
    model.setItems(new ArrayList<>(orderItems));
    model.recalculateTotals();
    saveClicked = true;
    closeDialog();
  }

  @FXML
  private void handleAddItem() {
    final Product product = productComboBox.getValue();
    final String quantityText = quantityField.getText();
    final StringBuilder errors = new StringBuilder();

    if (product == null) {
      errors.append("Product must be selected.\n");
    }
    int quantity = -1;
    try {
      quantity = Integer.parseInt(quantityText.trim());
      if (quantity <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      errors.append("Quantity must be a positive integer.\n");
    }

    if (errors.length() > 0) {
      showError(errors.toString());
      if (product == null) {
        productComboBox.requestFocus();
      } else {
        quantityField.requestFocus();
      }
      return;
    }

    // Prevent duplicates
    boolean alreadyAdded =
        orderItems.stream()
            .map(OrderItem::getProductId)
            .anyMatch(id -> Objects.equals(id, product.getId()));
    if (alreadyAdded) {
      showError("This product has already been added.");
      return;
    }

    OrderItem newItem = new OrderItem(product, quantity);
    orderItems.add(newItem);

    // Reset inputs
    quantityField.clear();
    productComboBox.getSelectionModel().clearSelection();
    productComboBox.requestFocus();
  }

  @FXML
  protected void handleCancel() {
    saveClicked = false;
    closeDialog();
  }

  private void loadCustomers() {
    try {
      final List<Customer> customers = customerService.getAllCustomers();
      customerComboBox.setItems(FXCollections.observableArrayList(customers));
    } catch (SQLException e) {
      AlertHelper.showDatabaseError(dialogStage, "Failed to load customers.", e);
      customerComboBox.setDisable(true);
    }
  }

  private void loadProducts() {
    try {
      final List<Product> products = productService.getAllProducts();
      productComboBox.setItems(FXCollections.observableArrayList(products));
      for (Product product : products) {
        productCache.put(product.getId(), product);
      }
    } catch (SQLException e) {
      AlertHelper.showDatabaseError(dialogStage, "Failed to load products.", e);
      productComboBox.setDisable(true);
      addItemButton.setDisable(true);
    }
  }

  private void updateAddItemButtonState() {
    Product selectedProduct = productComboBox.getValue();
    String quantityText = quantityField.getText();

    boolean isQuantityValid = false;
    try {
      int quantity = Integer.parseInt(quantityText.trim());
      isQuantityValid = quantity > 0;
    } catch (NumberFormatException ignored) {
    }

    addItemButton.setDisable(selectedProduct == null || !isQuantityValid);
  }

  private void updateSaveButtonState() {
    boolean customerSelected = customerComboBox.getValue() != null;
    boolean hasItems = !orderItems.isEmpty();
    boolean hasPaymentMethod =
        paymentMethodField.getText() != null && !paymentMethodField.getText().trim().isEmpty();
    boolean hasDate = datePicker.getValue() != null;
    saveButton.setDisable(!(customerSelected && hasItems && hasPaymentMethod && hasDate));
  }

  private void updateTotals() {
    int totalQuantity = orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    double totalAmount =
        orderItems.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
    totalItemsLabel.setText(String.valueOf(totalQuantity));
    totalAmountLabel.setText(TableCellUtils.formatPrice(totalAmount));
  }

  /**
   * Opens a custom dialog to edit the quantity of the specified OrderItem. Performs validation and
   * updates the quantity if valid.
   */
  private void showEditQuantityDialog(OrderItem item) {
    try {
      FXMLLoader loader = ViewLoader.loadFxml(DialogView.EDIT_QUANTITY_DIALOG);
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(this.dialogStage);
      Scene scene = new Scene(loader.load());
      scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
      dialogStage.setScene(scene);

      EditQuantityDialogController controller = loader.getController();
      controller.setDialogStage(dialogStage);
      controller.setModel(item);

      dialogStage.showAndWait();

      if (controller.isSaveClicked()) {
        updateTotals();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void deleteOrderItem(OrderItem item) {
    boolean confirmed =
        AlertHelper.showConfirmationAlert(
            dialogStage, "Delete Confirmation", null, "Are you sure you want to delete this item?");
    if (confirmed) {
      orderItems.remove(item);
      updateTotals();
    }
  }
}
