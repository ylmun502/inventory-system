package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
  private final ProductDAO productDAO;

  public ProductService() {
    this(getConnectionSafely());
  }

  public ProductService(Connection connection) {
    this.productDAO = new ProductDAO(connection);
  }

  private static Connection getConnectionSafely() {
    try {
      return DatabaseManager.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to initialize ProductService", e);
    }
  }

  public List<Product> getAllProducts() throws SQLException {
    return productDAO.getAllProducts();
  }

  public void addProduct(Product product) throws SQLException {
    productDAO.addProduct(product);
  }

  public void updateProduct(Product product) throws SQLException {
    productDAO.updateProduct(product);
  }

  public void deleteProduct(int productId) throws SQLException {
    productDAO.deleteProduct(productId);
  }

  public Product getById(int productId) throws SQLException {
    return productDAO.getById(productId);
  }

  public void decrementStock(int productId, int amount)
      throws SQLException, InsufficientStockException {
    Product product = productDAO.getById(productId);
    if (product == null) {
      throw new SQLException("Product not found: id=" + productId);
    }
    if (product.getStock() < amount) {
      throw new InsufficientStockException(null);
    }
    productDAO.decrementStock(productId, amount);
  }
}
