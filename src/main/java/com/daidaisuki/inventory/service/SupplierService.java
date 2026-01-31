package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.SupplierDAO;
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {
  private final SupplierDAO supplierDAO;

  public SupplierService(Connection connection) {
    this.supplierDAO = new SupplierDAO(connection);
  }

  public List<Supplier> listAll() throws SQLException {
    return supplierDAO.findAll();
  }

  public Supplier creatSupplier(Supplier supplier) throws SQLException {
    if (this.supplierDAO.existsByShortCode(supplier.getShortCode())) {
      throw new IllegalArgumentException("A supplier with this short code already exists.");
    }
    return supplierDAO.save(supplier);
  }
}
