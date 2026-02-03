package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.SupplierDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {
  private final TransactionManager transactionManager;
  private final SupplierDAO supplierDAO;

  public SupplierService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.supplierDAO = new SupplierDAO(connection);
  }

  public List<Supplier> listAll() throws SQLException {
    return supplierDAO.findAll();
  }

  public void createSupplier(Supplier supplier) throws SQLException {
    if (this.supplierDAO.existsByShortCode(supplier.getShortCode())) {
      throw new IllegalArgumentException("A supplier with this short code already exists.");
    }
    transactionManager.executeInTransaction(() -> supplierDAO.save(supplier));
  }

  public void updateSupplier(Supplier supplier) throws SQLException {
    transactionManager.executeInTransaction(() -> supplierDAO.update(supplier));
  }

  public void removeSupplier(int supplierId) throws SQLException {
    transactionManager.executeInTransaction(() -> supplierDAO.delete(supplierId));
  }
}
