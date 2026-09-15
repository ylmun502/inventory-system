package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.InventoryTransactionDAO;
import com.daidaisuki.inventory.dao.impl.ProductDAO;
import com.daidaisuki.inventory.dao.impl.StockBatchDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.enums.TransactionType;
<<<<<<< HEAD
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.InventoryTransaction;
=======
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.model.Product;
>>>>>>> origin/new
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.model.dto.StockAdjustRequest;
import com.daidaisuki.inventory.model.dto.StockAllocation;
import com.daidaisuki.inventory.model.dto.StockDeductRequest;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.model.dto.StockReturnRequest;
import java.math.BigDecimal;
<<<<<<< HEAD
import java.sql.Connection;
import java.sql.SQLException;
=======
import java.math.RoundingMode;
import java.sql.Connection;
>>>>>>> origin/new
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

<<<<<<< HEAD
  public void stockAdjust(StockAdjustRequest adjustRequest, int userId) throws SQLException {
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
=======
  public void stockAdjust(StockAdjustRequest adjustRequest, int userId) {
    transactionManager.executeInTransaction(
        () -> {
          if (adjustRequest.changeAmount() > 0) {
            if (adjustRequest.unitCost() == null
                || adjustRequest.unitCost().compareTo(BigDecimal.ZERO) == 0) {
              this.applyStockChange(adjustRequest.productId(), adjustRequest.changeAmount());
            } else {
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
            }
>>>>>>> origin/new
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

<<<<<<< HEAD
  public List<StockBatch> listInventoryByProduct(int productId) throws SQLException {
    return this.stockBatchDAO.findAllByProductId(productId);
  }

  public void receiveNewStock(StockReceiveRequest request, int userId) throws SQLException {
=======
  public List<StockBatch> listInventoryByProduct(int productId) {
    return this.stockBatchDAO.findAllByProductId(productId);
  }

  public void receiveNewStock(StockReceiveRequest request, int userId) {
>>>>>>> origin/new
    transactionManager.executeInTransaction(
        () -> {
          receiveNewStockInternal(request, userId);
        });
  }

<<<<<<< HEAD
  private void receiveNewStockInternal(StockReceiveRequest request, int userId)
      throws SQLException {
    this.applyStockChange(request.productId(), request.quantity());
    StockBatch newBatch =
        new StockBatch(
            request.productId(), request.supplierId(), request.quantity(), request.unitCost());
    StockBatch savedBatch = this.stockBatchDAO.save(newBatch);
=======
  private void receiveNewStockInternal(StockReceiveRequest request, int userId) {
    Product product =
        this.productDAO
            .findById(request.productId())
            .orElseThrow(() -> new EntityNotFoundException("The product could not be found."));
    int currentStock = product.getCurrentStock();
    BigDecimal currentAverageUnitCost = product.getAverageUnitCost();
    int incomingQuantity = request.quantity();
    BigDecimal incomingUnitCost = request.unitCost();
    BigDecimal totalValue =
        currentAverageUnitCost
            .multiply(BigDecimal.valueOf(currentStock))
            .add(incomingUnitCost.multiply(BigDecimal.valueOf(incomingQuantity)));
    int totalQuantity = currentStock + incomingQuantity;
    BigDecimal newAverageUnitCost =
        totalQuantity > 0
            ? totalValue.divide(BigDecimal.valueOf(totalQuantity), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    boolean success =
        this.productDAO.updateStockTotalAndCost(
            request.productId(), incomingQuantity, newAverageUnitCost);
    if (!success) {
      throw new DataAccessException("Stock update failed.");
    }
    StockBatch newBatch =
        StockBatch.createNew(
            request.productId(),
            request.supplierId(),
            request.batchCode(),
            request.expiryDate(),
            request.quantity(),
            request.unitCost());

    StockBatch savedBatch = this.stockBatchDAO.save(newBatch);

>>>>>>> origin/new
    this.logTransaction(
        request.productId(),
        savedBatch.getId(),
        userId,
        SYSTEM_REFERENCE_ID,
        request.quantity(),
        TransactionType.STOCK_IN,
        request.reason());
  }

<<<<<<< HEAD
  public List<InventoryTransaction> getTransactionHistory(int productId) throws SQLException {
    return this.inventoryTransactionDAO.findAllByProductId(productId);
  }

  public void processReturn(StockReturnRequest returnRequest, int userId) throws SQLException {
=======
  public List<InventoryTransaction> getTransactionHistory(int productId) {
    return this.inventoryTransactionDAO.findAllByProductId(productId);
  }

  public void processReturn(StockReturnRequest returnRequest, int userId) {
>>>>>>> origin/new
    transactionManager.executeInTransaction(
        () -> {
          this.applyStockChange(returnRequest.productId(), returnRequest.quantity());
          boolean success =
              this.stockBatchDAO.updateStockTotal(
                  returnRequest.batchId(), returnRequest.quantity());
          if (!success) {
<<<<<<< HEAD
            throw new SQLException("Batch not found: " + returnRequest.batchId());
=======
            throw new DataAccessException("The stock batch could not be found.");
>>>>>>> origin/new
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

<<<<<<< HEAD
  private List<StockAllocation> deductFromInventoryInternal(StockDeductRequest request, int userId)
      throws SQLException {
=======
  private List<StockAllocation> deductFromInventoryInternal(
      StockDeductRequest request, int userId) {
>>>>>>> origin/new
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
<<<<<<< HEAD
        throw new SQLException("Concurrent inventory change detected. Please retry.");
=======
        throw new DataAccessException("Concurrent inventory change detected. Please retry.");
>>>>>>> origin/new
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

<<<<<<< HEAD
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
=======
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
>>>>>>> origin/new
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
<<<<<<< HEAD
      String reason)
      throws SQLException {
=======
      String reason) {
>>>>>>> origin/new
    InventoryTransaction transaction =
        new InventoryTransaction(
            -1, productId, batchId, userId, referenceId, quantity, type, reason, null, null, false);
    this.inventoryTransactionDAO.save(transaction);
  }
}
