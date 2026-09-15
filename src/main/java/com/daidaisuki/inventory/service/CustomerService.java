package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.CustomerDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
<<<<<<< HEAD
=======
import com.daidaisuki.inventory.interfaces.Archivable;
import com.daidaisuki.inventory.interfaces.Removable;
>>>>>>> origin/new
import com.daidaisuki.inventory.model.Customer;
import java.sql.Connection;
import java.util.List;

<<<<<<< HEAD
public class CustomerService {
=======
public class CustomerService implements Archivable, Removable {
>>>>>>> origin/new
  private final TransactionManager transactionManager;
  private final CustomerDAO customerDAO;

  public CustomerService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.customerDAO = new CustomerDAO(connection);
  }

<<<<<<< HEAD
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
=======
  public List<Customer> listAll() {
    return customerDAO.findAll();
  }

  public void createCustomer(Customer customer) {
    transactionManager.executeInTransaction(() -> customerDAO.save(customer));
  }

  public void updateCustomer(Customer customer) {
    transactionManager.executeInTransaction(() -> customerDAO.update(customer));
  }

  public void archive(int customerId) {
    transactionManager.executeInTransaction(() -> customerDAO.archive(customerId));
  }

  public void restore(int customerId) {
    transactionManager.executeInTransaction(() -> customerDAO.restore(customerId));
  }

  public void remove(int customerId) {
    transactionManager.executeInTransaction(() -> customerDAO.delete(customerId));
  }

  public Customer getCustomer(int customerId) {
    return customerDAO
        .findById(customerId)
        .orElseThrow(() -> new EntityNotFoundException("The customer could not be found."));
>>>>>>> origin/new
  }
}
