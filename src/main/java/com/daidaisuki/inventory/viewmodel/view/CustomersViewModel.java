package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.service.CustomerService;
import com.daidaisuki.inventory.service.OrderService;
import com.daidaisuki.inventory.util.CurrencyUtil;
import com.daidaisuki.inventory.util.DateUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomersViewModel extends BaseListViewModel<Customer> {
  private final CustomerService customerService;
  private final OrderService orderService;

  private final ObservableList<Order> selectedCustomerOrders = FXCollections.observableArrayList();

  private final StringProperty fullNameText = new SimpleStringProperty();
  private final StringProperty emailText = new SimpleStringProperty();
  private final StringProperty phoneNumberText = new SimpleStringProperty();
  private final StringProperty addressText = new SimpleStringProperty();
  private final StringProperty totalOrderText = new SimpleStringProperty();
  private final StringProperty totalSpentText = new SimpleStringProperty();
  private final StringProperty totalDiscountText = new SimpleStringProperty();
  private final StringProperty averageOrderValueText = new SimpleStringProperty();
  private final StringProperty acquisitionSourceText = new SimpleStringProperty();
  private final StringProperty createdAtText = new SimpleStringProperty();
  private final StringProperty updatedAtText = new SimpleStringProperty();

  public CustomersViewModel(CustomerService customerService, OrderService orderService) {
    this.customerService = customerService;
    this.orderService = orderService;
    this.clearPresentation();
    this.setupSelectedItemListener();
    this.sortedList.setComparator(Comparator.comparing(Customer::getFullName));
  }

  private void setupSelectedItemListener() {
    this.selectedItem.addListener(
        (obs, oldCustomer, newCustomer) -> {
          if (newCustomer != null) {
            this.updatePresentation(newCustomer);
          } else {
            this.selectedCustomerOrders.clear();
            this.clearPresentation();
          }
        });
  }

  private void updatePresentation(Customer customer) {
    this.fullNameText.set(customer.getFullName());
    this.emailText.set("Email: " + customer.getEmail());
    this.phoneNumberText.set(customer.getPhoneNumber());
    this.addressText.set(customer.getAddress());
    this.totalOrderText.set(String.valueOf(customer.getTotalOrders()));
    this.totalSpentText.set(CurrencyUtil.format(customer.getTotalSpent()));
    this.totalDiscountText.set(CurrencyUtil.format(customer.getTotalDiscount()));
    this.averageOrderValueText.set(CurrencyUtil.format(customer.getAverageOrderValue()));
    this.acquisitionSourceText.set("Acquisition Source: " + customer.getAcquisitionSource());
    this.createdAtText.set("Created: " + DateUtils.format(customer.getCreatedAt()));
    this.updatedAtText.set("Updated: " + DateUtils.format(customer.getUpdatedAt()));
  }

  private void clearPresentation() {
    this.fullNameText.set("Select a Customer");
    this.emailText.set("Email: --");
    this.phoneNumberText.set("Phone: --");
    this.addressText.set("Address: --");
    this.totalOrderText.set("0");
    this.totalSpentText.set("$0.00");
    this.totalDiscountText.set("$0.00");
    this.averageOrderValueText.set("$0.00");
    this.acquisitionSourceText.set("Acquisition Source: --");
    this.createdAtText.set("Created: --");
    this.updatedAtText.set("Updated: --");
  }

  @Override
  protected List<Customer> fetchItems() throws SQLException {
    return this.customerService.listAll();
  }

  @Override
  protected boolean matchesSearch(Customer customer, String filterText) {
    return Stream.of(customer.getFullName(), customer.getAcquisitionSource())
        .filter(Objects::nonNull)
        .map(String::toLowerCase)
        .anyMatch(string -> string.contains(filterText));
  }

  @Override
  protected boolean isArchived(Customer customer) {
    return customer.isDeleted();
  }

  @Override
  public void add(Customer customer) {
    this.runAsync(() -> this.customerService.createCustomer(customer), null);
  }

  @Override
  public void update(Customer customer) {
    this.runAsync(() -> this.customerService.updateCustomer(customer), null);
  }

  @Override
  public void archive(Customer customer) {
    this.runAsync(() -> this.customerService.archive(customer.getId()), null);
  }

  @Override
  public void restore(Customer customer) {
    this.runAsync(() -> this.customerService.restore(customer.getId()), null);
  }

  @Override
  public void delete(Customer customer) {
    this.runAsync(() -> this.customerService.remove(customer.getId()), null);
  }

  /* Continue after order view is completed
  public ObservableList<Order> getOrdersForCustomer(int customerId) {
    try {
      List<Order> orders = this.orderService.getOrdersForCustomer(customerId);
      return FXCollections.observableList(orders);
    } catch (Exception e) {
      e.printStackTrace();
      return FXCollections.emptyObservableList();
    }
  }*/

  public CustomerService getCustomerService() {
    return this.customerService;
  }

  public OrderService getOrderService() {
    return this.orderService;
  }

  public ObservableList<Order> getSelectedCustomerOrders() {
    return selectedCustomerOrders;
  }

  public StringProperty fullNameTextProperty() {
    return this.fullNameText;
  }

  public StringProperty emailTextProperty() {
    return this.emailText;
  }

  public StringProperty phoneNumberTextProperty() {
    return this.phoneNumberText;
  }

  public StringProperty addressTextProperty() {
    return this.addressText;
  }

  public StringProperty totalOrderTextProperty() {
    return this.totalOrderText;
  }

  public StringProperty totalSpentTextProperty() {
    return this.totalSpentText;
  }

  public StringProperty totalDiscountTextProperty() {
    return this.totalDiscountText;
  }

  public StringProperty averageOrderValueTextProperty() {
    return this.averageOrderValueText;
  }

  public StringProperty acquisitionSourceTextProperty() {
    return this.acquisitionSourceText;
  }

  public StringProperty createdAtTextProperty() {
    return this.createdAtText;
  }

  public StringProperty updatedAtTextProperty() {
    return this.updatedAtText;
  }
}
