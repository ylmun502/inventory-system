package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.OrderItemDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.model.OrderItem;
import com.daidaisuki.inventory.model.dto.StockAllocation;
import java.sql.Connection;
import java.util.List;

public class OrderItemService {
  private final TransactionManager transactionManager;
  private final OrderItemDAO orderItemDAO;

  public OrderItemService(Connection connection) {
    this.transactionManager = new TransactionManager(connection);
    this.orderItemDAO = new OrderItemDAO(connection);
  }

<<<<<<< HEAD
  public OrderItem createItem(OrderItem uiItem, int persistentOrderId, StockAllocation allocation)
      throws SQLException {
=======
  public OrderItem createItem(OrderItem uiItem, int persistentOrderId, StockAllocation allocation) {
>>>>>>> origin/new
    return transactionManager.executeInTransaction(
        () -> createItemInternal(uiItem, persistentOrderId, allocation));
  }

<<<<<<< HEAD
  public void removeOrderItem(int orderItemId) throws SQLException {
    transactionManager.executeInTransaction(() -> orderItemDAO.delete(orderItemId));
  }

  public List<OrderItem> listByOrderId(int orderId) throws SQLException {
=======
  public void removeOrderItem(int orderItemId) {
    transactionManager.executeInTransaction(() -> orderItemDAO.delete(orderItemId));
  }

  public List<OrderItem> listByOrderId(int orderId) {
>>>>>>> origin/new
    if (orderId <= 0) {
      throw new IllegalArgumentException("Order must be persisted to fetch items.");
    }
    return orderItemDAO.findAllByOrderId(orderId);
  }

<<<<<<< HEAD
  public void removeAllByOrderId(int orderId) throws SQLException {
    transactionManager.executeInTransaction(() -> removeAllByOrderIdInternal(orderId));
  }

  OrderItem createItemInternal(OrderItem uiItem, int persistentOrderId, StockAllocation allocation)
      throws SQLException {
=======
  public void removeAllByOrderId(int orderId) {
    transactionManager.executeInTransaction(() -> removeAllByOrderIdInternal(orderId));
  }

  OrderItem createItemInternal(
      OrderItem uiItem, int persistentOrderId, StockAllocation allocation) {
>>>>>>> origin/new
    // Need to update this later
    OrderItem itemToPersist =
        new OrderItem(
            persistentOrderId,
            uiItem.getProductId(),
            allocation.batchId(),
            allocation.quantity(),
            uiItem.getUnitPriceAtSaleCents(),
            0);
    return orderItemDAO.save(itemToPersist);
  }

<<<<<<< HEAD
  void removeAllByOrderIdInternal(int orderId) throws SQLException {
=======
  void removeAllByOrderIdInternal(int orderId) {
>>>>>>> origin/new
    orderItemDAO.deleteAllByOrderId(orderId);
  }
}
