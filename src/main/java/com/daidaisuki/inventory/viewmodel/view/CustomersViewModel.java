package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.service.CustomerService;
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

public class CustomersViewModel extends BaseListViewModel<Customer> {
  private final CustomerService customerService;

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

  public CustomersViewModel(CustomerService customerService) {
    this.customerService = customerService;
    this.clearPresentation();
    this.setupSelectedItemListener();
    this.sortedList.setComparator(
        Comparator.comparing(
            Customer::getFullName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
  }

  private void setupSelectedItemListener() {
    this.selectedItem.addListener(
        (obs, oldCustomer, newCustomer) -> {
          if (newCustomer != null) {
            this.updatePresentation(newCustomer);
          } else {
            this.clearPresentation();
          }
        });
  }

  private void updatePresentation(Customer customer) {
    this.fullNameText.set(valueOrPlaceholder(customer.getFullName()));
    this.emailText.set(valueOrPlaceholder(customer.getEmail()));
    this.phoneNumberText.set(valueOrPlaceholder(customer.getPhoneNumber()));
    this.addressText.set(valueOrPlaceholder(customer.getAddress()));
    this.acquisitionSourceText.set(valueOrPlaceholder(customer.getAcquisitionSource()));
    this.totalOrderText.set(String.valueOf(customer.getTotalOrders()));
    this.totalSpentText.set(CurrencyUtil.format(customer.getTotalSpent()));
    this.totalDiscountText.set(CurrencyUtil.format(customer.getTotalDiscount()));
    this.averageOrderValueText.set(CurrencyUtil.format(customer.getAverageOrderValue()));
    this.createdAtText.set(DateUtils.format(customer.getCreatedAt()));
    this.updatedAtText.set(DateUtils.format(customer.getUpdatedAt()));
  }

  private void clearPresentation() {
    this.fullNameText.set("Select a Customer");
    this.emailText.set("--");
    this.phoneNumberText.set("--");
    this.addressText.set("--");
    this.totalOrderText.set("0");
    this.totalSpentText.set("$0.00");
    this.totalDiscountText.set("$0.00");
    this.averageOrderValueText.set("$0.00");
    this.acquisitionSourceText.set("--");
    this.createdAtText.set("--");
    this.updatedAtText.set("--");
  }

  private String valueOrPlaceholder(String value) {
    return (value == null || value.isBlank()) ? "--" : value;
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

  public CustomerService getCustomerService() {
    return this.customerService;
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
