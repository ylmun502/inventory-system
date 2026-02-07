package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ProductService {
  private final TransactionManager transactionManager;
  private final ProductDAO productDAO;

  public ProductService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.productDAO = new ProductDAO(connection);
  }

  public List<Product> listProducts() throws SQLException {
    return this.productDAO.findAll();
  }

  public void createProduct(Product product) throws SQLException {
    if (this.productDAO.existsBySku(product.getSku())) {
      throw new IllegalArgumentException("A product with this sku already exists.");
    } else if (this.productDAO.existsByBarcode(product.getBarcode())) {
      throw new IllegalArgumentException("A product with this barcode already exists.");
    }
    if (product.getBarcode() == null || product.getBarcode().isEmpty()) {
      product.setBarcode(generateBarcode());
    }
    transactionManager.executeInTransaction(() -> this.productDAO.save(product));
  }

  private String generateBarcode() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  public void updateProduct(Product product) throws SQLException {
    transactionManager.executeInTransaction(() -> this.productDAO.update(product));
  }

  public void removeProduct(int productId) throws SQLException {
    transactionManager.executeInTransaction(() -> this.productDAO.delete(productId));
  }

  public Product getProduct(int productId) throws SQLException {
    return this.productDAO
        .findById(productId)
        .orElseThrow(() -> new SQLException("Product not found:" + productId));
  }

  public List<String> listDistinctUnitTypes() throws SQLException {
    return this.productDAO.findAllDistinctUnitTypes();
  }
}
