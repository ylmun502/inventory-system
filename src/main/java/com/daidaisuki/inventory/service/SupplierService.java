package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.SupplierDAO;
import com.daidaisuki.inventory.db.TransactionManager;
<<<<<<< HEAD
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {
=======
import com.daidaisuki.inventory.interfaces.Archivable;
import com.daidaisuki.inventory.interfaces.Removable;
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.util.List;

public class SupplierService implements Archivable, Removable {
>>>>>>> origin/new
  private final TransactionManager transactionManager;
  private final SupplierDAO supplierDAO;

  public SupplierService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.supplierDAO = new SupplierDAO(connection);
  }

<<<<<<< HEAD
  public List<Supplier> listAll() throws SQLException {
    return supplierDAO.findAll();
  }

  public void createSupplier(Supplier supplier) throws SQLException {
=======
  public List<Supplier> listAll() {
    return supplierDAO.findAll();
  }

  public void createSupplier(Supplier supplier) {
>>>>>>> origin/new
    if (this.supplierDAO.existsByShortCode(supplier.getShortCode())) {
      throw new IllegalArgumentException("A supplier with this short code already exists.");
    }
    transactionManager.executeInTransaction(() -> supplierDAO.save(supplier));
  }

<<<<<<< HEAD
  public void updateSupplier(Supplier supplier) throws SQLException {
    transactionManager.executeInTransaction(() -> supplierDAO.update(supplier));
  }

  public void removeSupplier(int supplierId) throws SQLException {
    transactionManager.executeInTransaction(() -> supplierDAO.delete(supplierId));
=======
  public void update(Supplier supplier) {
    transactionManager.executeInTransaction(() -> supplierDAO.update(supplier));
  }

  public void archive(int supplierId) {
    transactionManager.executeInTransaction(() -> supplierDAO.archive(supplierId));
  }

  public void restore(int supplierId) {
    transactionManager.executeInTransaction(() -> supplierDAO.restore(supplierId));
  }

  public void remove(int supplierId) {
    transactionManager.executeInTransaction(() -> supplierDAO.remove(supplierId));
>>>>>>> origin/new
  }
}
