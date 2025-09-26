package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.CustomerDAO;
import com.daidaisuki.inventory.dao.OrderDAO;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.dto.OrderStats;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {
  private final CustomerDAO customerDAO;
  private final OrderDAO orderDAO;

  public CustomerService(Connection connection) {
    this.customerDAO = new CustomerDAO(connection);
    this.orderDAO = new OrderDAO(connection);
  }

  public List<Customer> getAllCustomers() throws SQLException {
    return customerDAO.getAllCustomers();
  }

  public void addCustomer(Customer customer) throws SQLException {
    customerDAO.addCustomer(customer);
  }

  public void updateCustomer(Customer customer) throws SQLException {
    customerDAO.updateCustomer(customer);
  }

  public void deleteCustomer(int customerId) throws SQLException {
    customerDAO.deleteCustomer(customerId);
  }

  public Customer getById(int customerId) throws SQLException {
    return customerDAO.getById(customerId);
  }

  public Customer findByName(String name) throws SQLException {
    return customerDAO.findByName(name);
  }

  public void enrichCustomerStats(Customer customer) throws SQLException {
    OrderStats orderStats = orderDAO.getStatsForCustomer(customer.getId());
    customer.setTotalOrders(orderStats.getTotalOrders());
    customer.setTotalSpent(orderStats.getTotalSpent());
    customer.setTotalDiscount(orderStats.getTotalDiscount());
  }
}
