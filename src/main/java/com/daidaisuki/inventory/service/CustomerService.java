package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.CustomerDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.interfaces.Archivable;
import com.daidaisuki.inventory.interfaces.Removable;
import com.daidaisuki.inventory.model.Customer;
import java.sql.Connection;
import java.util.List;

public class CustomerService implements Archivable, Removable {
  private final TransactionManager transactionManager;
  private final CustomerDAO customerDAO;

  public CustomerService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.customerDAO = new CustomerDAO(connection);
  }

  public List<Customer> listAll() {
    return customerDAO.findAll();
  }

  public void createCustomer(Customer customer) {
    transactionManager.executeInTransaction(() -> customerDAO.save(customer));
  }

  public void updateCustomer(Customer customer) {
    transactionManager.executeInTransaction(() -> customerDAO.update(customer));
  }

  @Override
  public void archive(int customerId) {
    transactionManager.executeInTransaction(() -> customerDAO.archive(customerId));
  }

  @Override
  public void restore(int customerId) {
    transactionManager.executeInTransaction(() -> customerDAO.restore(customerId));
  }

  @Override
  public void remove(int customerId) {
    transactionManager.executeInTransaction(() -> customerDAO.remove(customerId));
  }

  public Customer getCustomer(int customerId) {
    return customerDAO
        .findById(customerId)
        .orElseThrow(() -> new EntityNotFoundException("The customer could not be found."));
  }
}
