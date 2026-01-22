package com.daidaisuki.inventory.viewmodel.view;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.service.CustomerService;
import com.daidaisuki.inventory.service.OrderService;
import com.daidaisuki.inventory.viewmodel.base.BaseListViewModel;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomersViewModel extends BaseListViewModel<Customer> {
  private final CustomerService customerService;
  private final OrderService orderService;

  public CustomersViewModel(CustomerService customerService, OrderService orderService) {
    this.customerService = customerService;
    this.orderService = orderService;
  }

  public CustomerService getCustomerService() {
    return this.customerService;
  }

  @Override
  protected List<Customer> fetchItems() throws SQLException {
    return this.customerService.listCustomers();
  }

  @Override
  public void add(Customer customer) throws SQLException {
    this.customerService.createCustomer(customer);
    refresh();
  }

  @Override
  public void update(Customer customer) throws SQLException {
    this.customerService.updateCustomer(customer);
    refresh();
  }

  @Override
  public void delete(Customer customer) throws SQLException {
    this.customerService.removeCustomer(customer.getId());
    refresh();
  }

  public ObservableList<Order> getOrdersForCustomer(int customerId) {
    try {
      List<Order> orders = this.orderService.getOrdersForCustomer(customerId);
      return FXCollections.observableList(orders);
    } catch (Exception e) {
      e.printStackTrace();
      return FXCollections.emptyObservableList();
    }
  }
}
