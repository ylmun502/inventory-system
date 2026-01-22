package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.CustomerDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.model.Customer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {
  private final TransactionManager transactionManager;
  private final CustomerDAO customerDAO;

  public CustomerService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.customerDAO = new CustomerDAO(connection);
  }

  public List<Customer> listCustomers() throws SQLException {
    return customerDAO.findAll();
  }

  public void createCustomer(Customer customer) throws SQLException {
    transactionManager.executeInTransaction(() -> customerDAO.save(customer));
  }

  public void updateCustomer(Customer customer) throws SQLException {
    transactionManager.executeInTransaction(() -> customerDAO.update(customer));
  }

  public void removeCustomer(int customerId) throws SQLException {
    transactionManager.executeInTransaction(() -> customerDAO.delete(customerId));
  }

  public Customer getCustomer(int customerId) throws SQLException {
    return customerDAO
        .findById(customerId)
        .orElseThrow(
            () -> new EntityNotFoundException("Customer ID " + customerId + " not found."));
  }

  public List<Customer> searchCustomersByName(String name) throws SQLException {
    return customerDAO.findAllByName(name);
  }
}
