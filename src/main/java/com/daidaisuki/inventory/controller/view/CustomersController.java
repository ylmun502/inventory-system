package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.App;
import com.daidaisuki.inventory.base.controller.BaseTableController;
import com.daidaisuki.inventory.controller.dialog.CustomerDialogController;
import com.daidaisuki.inventory.enums.DialogView;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.service.CustomerService;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.util.TableCellUtils;
import com.daidaisuki.inventory.util.TableColumnUtils;
import com.daidaisuki.inventory.util.ViewLoader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CustomersController extends BaseTableController<Customer> {
  @FXML private TableView<Customer> customerTable;
  @FXML private TableColumn<Customer, String> nameCol;
  @FXML private TableColumn<Customer, Integer> totalOrdersCol;
  @FXML private TableColumn<Customer, Double> totalSpentCol;
  @FXML private TableColumn<Customer, Double> totalDiscountCol;
  @FXML private TableColumn<Customer, String> platformCol;

  @FXML private Button addButton;
  @FXML private Button editButton;
  @FXML private Button deleteButton;

  private final CustomerService customerService = new CustomerService();

  @FXML
  public void initialize() {
    setupColumns();
    initializeBase(customerTable, addButton, editButton, deleteButton);
    nameCol.setSortType(TableColumn.SortType.ASCENDING);
    customerTable.getSortOrder().add(nameCol);
  }

  public void setupColumns() {
    List<Double> ratios = new ArrayList<>(Collections.nCopies(5, 0.2));
    TableColumnUtils.bindColumnWidthsByRatio(customerTable, ratios);
    nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    platformCol.setCellValueFactory(cellData -> cellData.getValue().platformProperty());
    totalOrdersCol.setCellValueFactory(
        cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalOrders()));
    totalSpentCol.setCellValueFactory(
        cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalSpent()));
    totalDiscountCol.setCellValueFactory(
        cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalDiscount()));

    nameCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
    platformCol.setCellFactory(TableCellUtils.centerAlignedStringCellFactory());
    totalOrdersCol.setCellFactory(TableCellUtils.centerAlignedIntegerCellFactory());
    totalSpentCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
    totalDiscountCol.setCellFactory(TableCellUtils.centerAlignedPriceCellFactory());
  }

  @Override
  protected List<Customer> fetchFromDB() throws SQLException {
    List<Customer> customers = customerService.getAllCustomers();
    for (Customer customer : customers) {
      customerService.enrichCustomerStats(customer);
    }
    return customers;
  }

  @Override
  protected Window getWindow() {
    return FxWindowUtils.getWindow(customerTable);
  }

  @Override
  protected void addItem(Customer customer) throws SQLException {
    customerService.addCustomer(customer);
  }

  @Override
  protected void updateItem(Customer customer) throws SQLException {
    customerService.updateCustomer(customer);
  }

  @Override
  protected void deleteItem(Customer customer) throws SQLException {
    customerService.deleteCustomer(customer.getId());
  }

  @Override
  protected Customer showDialog(Customer customerToEdit) {
    return showCustomerDialog(customerToEdit);
  }

  private Customer showCustomerDialog(Customer customerToEdit) {
    try {
      FXMLLoader loader = ViewLoader.loadFxml(DialogView.CUSTOMER_DIALOG);
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(customerTable.getScene().getWindow());
      Scene scene = new Scene(loader.load());
      scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
      dialogStage.setScene(scene);

      CustomerDialogController controller = loader.getController();
      controller.setDialogStage(dialogStage);
      controller.setModel(customerToEdit);

      dialogStage.showAndWait();

      return controller.isSaveClicked() ? controller.getModel() : null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @FXML
  private void handleViewOrders() {}
}
