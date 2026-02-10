package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.InventoryTransactionDAO;
import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.dao.impl.StockBatchDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.enums.TransactionType;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.dto.StockAdjustRequest;
import com.daidaisuki.inventory.model.dto.StockAllocation;
import com.daidaisuki.inventory.model.dto.StockDeductRequest;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.model.dto.StockReturnRequest;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
  private final TransactionManager transactionManager;
  private final ProductDAO productDAO;
  private final StockBatchDAO stockBatchDAO;
  private final InventoryTransactionDAO inventoryTransactionDAO;
  // Temporary dummy for user id and reference id
  // private static final int SYSTEM_USER_ID = 1;
  private static final int SYSTEM_REFERENCE_ID = 0;
  private static final int SYSTEM_SUPPLIER_ID = 0;

  public InventoryService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.productDAO = new ProductDAO(connection);
    this.stockBatchDAO = new StockBatchDAO(connection);
    this.inventoryTransactionDAO = new InventoryTransactionDAO(connection);
  }

  public void stockAdjust(StockAdjustRequest adjustRequest, int userId) {
    transactionManager.executeInTransaction(
        () -> {
          if (adjustRequest.changeAmount() > 0) {
            StockReceiveRequest receiveRequest =
                new StockReceiveRequest(
                    adjustRequest.productId(),
                    SYSTEM_SUPPLIER_ID,
                    null,
                    adjustRequest.changeAmount(),
                    BigDecimal.ZERO,
                    null,
                    adjustRequest.reason());
            this.receiveNewStockInternal(receiveRequest, userId);
          } else if (adjustRequest.changeAmount() < 0) {
            StockDeductRequest deductRequest =
                new StockDeductRequest(
                    adjustRequest.productId(),
                    Math.abs(adjustRequest.changeAmount()),
                    adjustRequest.type(),
                    adjustRequest.reason());
            this.deductFromInventoryInternal(deductRequest, userId);
          }
        });
  }

  public List<StockBatch> listInventoryByProduct(int productId) {
    return this.stockBatchDAO.findAllByProductId(productId);
  }

  public void receiveNewStock(StockReceiveRequest request, int userId) {
    transactionManager.executeInTransaction(
        () -> {
          receiveNewStockInternal(request, userId);
        });
  }

  private void receiveNewStockInternal(StockReceiveRequest request, int userId) {
    this.applyStockChange(request.productId(), request.quantity());
    StockBatch newBatch =
        StockBatch.createNew(
            request.productId(),
            request.supplierId(),
            request.batchCode(),
            request.expiryDate(),
            request.quantity(),
            request.unitCost());

    StockBatch savedBatch = this.stockBatchDAO.save(newBatch);
    this.logTransaction(
        request.productId(),
        savedBatch.getId(),
        userId,
        SYSTEM_REFERENCE_ID,
        request.quantity(),
        TransactionType.STOCK_IN,
        request.reason());
  }

  public List<InventoryTransaction> getTransactionHistory(int productId) {
    return this.inventoryTransactionDAO.findAllByProductId(productId);
  }

  public void processReturn(StockReturnRequest returnRequest, int userId) {
    transactionManager.executeInTransaction(
        () -> {
          this.applyStockChange(returnRequest.productId(), returnRequest.quantity());
          boolean success =
              this.stockBatchDAO.updateStockTotal(
                  returnRequest.batchId(), returnRequest.quantity());
          if (!success) {
            throw new DataAccessException("The stock batch could not be found.");
          }
          this.logTransaction(
              returnRequest.productId(),
              returnRequest.batchId(),
              userId,
              returnRequest.orderId(),
              returnRequest.quantity(),
              TransactionType.RETURN,
              returnRequest.reason());
        });
  }

  private List<StockAllocation> deductFromInventoryInternal(
      StockDeductRequest request, int userId) {
    this.applyStockChange(request.productId(), -request.quantity());
    List<StockAllocation> allocations = new ArrayList<>();
    List<StockBatch> batches = this.stockBatchDAO.findAllAvailableByProductId(request.productId());
    int remainingAmount = request.quantity();
    for (StockBatch batch : batches) {
      if (remainingAmount <= 0) {
        break;
      }
      int takeAmount = Math.min(batch.getQuantityRemaining(), remainingAmount);
      boolean success = this.stockBatchDAO.updateStockTotal(batch.getId(), -takeAmount);
      if (!success) {
        throw new DataAccessException("Concurrent inventory change detected. Please retry.");
      }
      this.logTransaction(
          request.productId(),
          batch.getId(),
          userId,
          SYSTEM_REFERENCE_ID,
          -takeAmount,
          request.type(),
          request.reason());
      allocations.add(new StockAllocation(batch.getId(), takeAmount, batch.getUnitCost()));
      remainingAmount -= takeAmount;
    }
    return allocations;
  }

  private void applyStockChange(int productId, int amount) {
    boolean success = this.productDAO.updateStockTotal(productId, amount);
    if (!success) {
      if (amount >= 0) {
        throw new EntityNotFoundException("The product could not be found.");
      } else {
        boolean exist = this.productDAO.exists(productId);
        if (exist) {
          throw new InsufficientStockException("Insufficient stock for the product.");
        } else {
          throw new EntityNotFoundException("The product could not be found.");
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
      TransactionType type,
      String reason) {
    InventoryTransaction transaction =
        new InventoryTransaction(
            -1, productId, batchId, userId, referenceId, quantity, type, reason, null, null, false);
    this.inventoryTransactionDAO.save(transaction);
  }
}
