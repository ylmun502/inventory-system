package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.db.TransactionManager;
<<<<<<< HEAD
=======
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.interfaces.Archivable;
import com.daidaisuki.inventory.interfaces.Removable;
>>>>>>> origin/new
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

<<<<<<< HEAD
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

=======
public class ProductService implements Archivable, Removable {
  private final TransactionManager transactionManager;
  private final ProductDAO productDAO;

>>>>>>> origin/new
  public ProductService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.productDAO = new ProductDAO(connection);
  }

<<<<<<< HEAD
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
=======
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

  @Override
  public void archive(int productId) {
    transactionManager.executeInTransaction(() -> this.productDAO.archive(productId));
  }

  @Override
  public void restore(int productId) {
    transactionManager.executeInTransaction(() -> this.productDAO.restore(productId));
  }

  @Override
  public void remove(int productId) {
    transactionManager.executeInTransaction(() -> this.productDAO.remove(productId));
  }

  public Product getProduct(int productId) {
    return this.productDAO
        .findById(productId)
        .orElseThrow(() -> new DataAccessException("The product could not be found"));
  }

  public List<String> listDistinctUnitTypes() {
    return this.productDAO.findAllDistinctUnitTypes();
>>>>>>> origin/new
  }
}
