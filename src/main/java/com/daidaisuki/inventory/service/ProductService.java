package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class ProductService {
  private final TransactionManager transactionManager;
  private final ProductDAO productDAO;

  public ProductService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.productDAO = new ProductDAO(connection);
  }

  public List<Product> listProducts() {
    return this.productDAO.findAll();
  }

  public void createProduct(Product product) {
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

  public void updateProduct(Product product) {
    transactionManager.executeInTransaction(() -> this.productDAO.update(product));
  }

  public void archiveProduct(int productId) {
    transactionManager.executeInTransaction(() -> this.productDAO.archive(productId));
  }

  public void restoreProduct(int productId) {
    transactionManager.executeInTransaction(() -> this.productDAO.restore(productId));
  }

  public void removeProduct(int productId) {
    transactionManager.executeInTransaction(() -> this.productDAO.remove(productId));
  }

  public Product getProduct(int productId) {
    return this.productDAO
        .findById(productId)
        .orElseThrow(() -> new DataAccessException("The product could not be found"));
  }

  public List<String> listDistinctUnitTypes() {
    return this.productDAO.findAllDistinctUnitTypes();
  }
}
