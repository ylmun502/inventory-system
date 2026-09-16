package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.SupplierDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.interfaces.Archivable;
import com.daidaisuki.inventory.interfaces.Removable;
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.util.List;

public class SupplierService implements Archivable, Removable {
  private final TransactionManager transactionManager;
  private final SupplierDAO supplierDAO;

  public SupplierService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.supplierDAO = new SupplierDAO(connection);
  }

  public List<Supplier> listAll() {
    return supplierDAO.findAll();
  }

  public void createSupplier(Supplier supplier) {
    if (this.supplierDAO.existsByShortCode(supplier.getShortCode())) {
      throw new IllegalArgumentException("A supplier with this short code already exists.");
    }
    transactionManager.executeInTransaction(() -> supplierDAO.save(supplier));
  }

  public void update(Supplier supplier) {
    if (this.supplierDAO.existsByShortCodeExcludingId(supplier.getShortCode(), supplier.getId())) {
      throw new IllegalArgumentException("A supplier with this short code already exists.");
    }
    transactionManager.executeInTransaction(() -> supplierDAO.update(supplier));
  }

  @Override
  public void archive(int supplierId) {
    transactionManager.executeInTransaction(() -> supplierDAO.archive(supplierId));
  }

  @Override
  public void restore(int supplierId) {
    transactionManager.executeInTransaction(() -> supplierDAO.restore(supplierId));
  }

  @Override
  public void remove(int supplierId) {
    transactionManager.executeInTransaction(() -> supplierDAO.remove(supplierId));
  }
}
