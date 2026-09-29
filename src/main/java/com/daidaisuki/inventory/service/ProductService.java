package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.interfaces.Archivable;
import com.daidaisuki.inventory.interfaces.Removable;
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class ProductService implements Archivable, Removable {
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
    this.transactionManager.executeInTransaction(
        () -> {
          this.validateSkuForCreate(product);
          if (product.getBarcode() == null || product.getBarcode().isBlank()) {
            product.setBarcode(this.generateUniqueBarcode());
          } else {
            this.validateBarcodeForCreate(product);
          }
          this.productDAO.save(product);
        });
  }

  public void updateProduct(Product product) {
    this.transactionManager.executeInTransaction(
        () -> {
          this.validateSkuForUdate(product);
          this.validateBarcodeForUpdate(product);
          this.productDAO.update(product);
        });
  }

  private void validateSkuForCreate(Product product) {
    if (this.productDAO.existsBySku(product.getSku())) {
      throw new IllegalArgumentException("A product with this SKU already exists.");
    }
  }

  private void validateBarcodeForCreate(Product product) {
    if (this.productDAO.existsByBarcode(product.getBarcode())) {
      throw new IllegalArgumentException("A product with this barcode already exists.");
    }
  }

  private void validateSkuForUdate(Product product) {
    if (this.productDAO.existsBySkuExcludingId(product.getSku(), product.getId())) {
      throw new IllegalArgumentException("A product with this SKU already exists.");
    }
  }

  private void validateBarcodeForUpdate(Product product) {
    if (this.productDAO.existByBarcodeExcludingId(product.getBarcode(), product.getId())) {
      throw new IllegalArgumentException("A product with this barcode already exists.");
    }
  }

  private String generateUniqueBarcode() {
    String barcode;
    do {
      barcode = generateBarcode();
    } while (this.productDAO.existsByBarcode(barcode));
    return barcode;
  }

  private String generateBarcode() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  @Override
  public void archive(int productId) {
    this.transactionManager.executeInTransaction(() -> this.productDAO.archive(productId));
  }

  @Override
  public void restore(int productId) {
    this.transactionManager.executeInTransaction(() -> this.productDAO.restore(productId));
  }

  @Override
  public void remove(int productId) {
    this.transactionManager.executeInTransaction(() -> this.productDAO.remove(productId));
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
