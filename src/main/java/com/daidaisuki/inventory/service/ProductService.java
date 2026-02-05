package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
  private final TransactionManager transactionManager;
  private final ProductDAO productDAO;

  /* Will migrate to view model later and change the name to InventoryService
  private final ObservableList<StockBatch> masterList = FXCollections.observableArrayList();
  private final SortedList<StockBatch> fifoView =
      new SortedList<>(
          masterList,
          Comparator.comparing(StockBatch::getCreatedAt).thenComparing(StockBatch::getId));

  public ObservableList<StockBatch> getMasterList() {
    return masterList;
  }

  public void loadInventory(int productId) throws SQLException {
    List<StockBatch> data = batchDAO.fetchByProductId(productId);
    masterList.setAll(data);
  }

  public SortedList<StockBatch> getFifoView() {
    return fifoView;
  }
  */

  public ProductService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.productDAO = new ProductDAO(connection);
  }

  public List<Product> listProducts() throws SQLException {
    return productDAO.findAll();
  }

  public void createProduct(Product product) throws SQLException {
    if (productDAO.existsBySku(product.getSku())) {
      throw new IllegalArgumentException("A product with this sku already exists.");
    } else if (productDAO.existsByBarcode(product.getBarcode())) {
      throw new IllegalArgumentException("A product with this barcode already exists.");
    }
    transactionManager.executeInTransaction(() -> productDAO.save(product));
  }

  public void updateProduct(Product product) throws SQLException {
    transactionManager.executeInTransaction(() -> productDAO.update(product));
  }

  public void removeProduct(int productId) throws SQLException {
    transactionManager.executeInTransaction(() -> productDAO.delete(productId));
  }

  public Product getProduct(int productId) throws SQLException {
    return productDAO
        .findById(productId)
        .orElseThrow(() -> new SQLException("Product not found:" + productId));
  }
}
