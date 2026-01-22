package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.InventoryTransactionDAO;
import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.dao.impl.StockBatchDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.dto.StockAllocation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
  private final TransactionManager transactionManager;
  private final ProductDAO productDAO;
  private final StockBatchDAO stockBatchDAO;
  private final InventoryTransactionDAO inventoryTransactionDAO;
  // Temporary dummy for user id and reference id
  public static final int SYSTEM_USER_ID = 1;
  public static final int SYSTEM_REFERENCE_ID = 0;

  public InventoryService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.productDAO = new ProductDAO(connection);
    this.stockBatchDAO = new StockBatchDAO(connection);
    this.inventoryTransactionDAO = new InventoryTransactionDAO(connection);
  }

  /* will be deleted soon
    public void incrementStock(int productId, int quantity) throws SQLException {
      transactionManager.executeInTransaction(() -> incrementStockInternal(productId, quantity));
    }

    public void decrementStock(int productId, int quantity)
        throws SQLException, InsufficientStockException {
      transactionManager.executeInTransaction(() -> decrementStockInternal(productId, quantity));
    }
  */
  private List<StockAllocation> deductFromInventoryTransactional(
      int productId, int userId, int quantity, String type, String reason) throws SQLException {
    return transactionManager.executeInTransaction(
        () -> deductFromInventory(productId, userId, quantity, type, reason));
  }

  private void returnToInventoryTransactional(int productId, int orderId, int batchId, int quantity)
      throws SQLException {
    transactionManager.executeInTransaction(
        () -> returnToInventory(productId, orderId, batchId, quantity));
  }

  public void stockAdjust(int productId, int userId, int changeAmount, String type, String reason)
      throws SQLException {
    transactionManager.executeInTransaction(
        () -> {
          if (changeAmount > 0) {
            returnToInventory(productId, userId, changeAmount, type, reason);
          } else {
            deductFromInventory(productId, userId, changeAmount, type, reason);
          }
        });
  }

  public List<StockBatch> listInventoryByProduct(int productId) throws SQLException {
    return stockBatchDAO.findAllByProductId(productId);
  }

  public void returnToInventory(int productId, int orderId, int batchId, int quantity)
      throws SQLException {
    this.applyStockChange(productId, quantity);
    this.stockBatchDAO.incrementQuantity(batchId, quantity);
    this.logTransaction(productId, batchId, orderId, batchId, quantity, null, null);
    /*
    inventoryTransactionDAO.add(
        productId,
        batchId,
        quantity,
        "REVERT",
        orderId // Reference the order so we know why it's back
    );*/
  }

  public List<StockAllocation> deductFromInventory(
      int productId, int userId, int quantity, String type, String reason) throws SQLException {
    this.applyStockChange(productId, -quantity);
    List<StockAllocation> allocations = new ArrayList<>();
    List<StockBatch> batches = this.stockBatchDAO.findAllAvailableByProductId(productId);
    int remainingAmount = quantity;
    for (StockBatch batch : batches) {
      if (remainingAmount <= 0) {
        break;
      }
      int takeAmount = Math.min(batch.getQuantityRemaining(), remainingAmount);
      boolean success = this.stockBatchDAO.decrementQuantity(batch.getId(), takeAmount);
      if (!success) {
        throw new SQLException("Concurrent inventory change detected. Please retry.");
      }
      this.logTransaction(
          productId, batch.getId(), SYSTEM_USER_ID, SYSTEM_REFERENCE_ID, -takeAmount, type, reason);
      allocations.add(new StockAllocation(batch.getId(), takeAmount, batch.getUnitCost()));
      remainingAmount -= takeAmount;
    }
    return allocations;
  }

  private void applyStockChange(int productId, int amount) throws SQLException {
    boolean success = this.productDAO.updateStockTotal(productId, amount);
    if (!success) {
      if (amount >= 0) {
        throw new EntityNotFoundException("Product ID " + productId + " not found.");
      } else {
        boolean exist = this.productDAO.exists(productId);
        if (exist) {
          throw new InsufficientStockException("Insufficient stock for Product ID " + productId);
        } else {
          throw new EntityNotFoundException("Product ID " + productId + " not found.");
        }
      }
    }
  }

  private void logTransaction(
      int productId,
      int batchId,
      int userId,
      int referenceId,
      int quantity,
      String type,
      String reason)
      throws SQLException {
    InventoryTransaction transaction =
        new InventoryTransaction(
            -1, productId, batchId, userId, referenceId, quantity, type, reason, null, null, false);
    this.inventoryTransactionDAO.save(transaction);
  }
}
