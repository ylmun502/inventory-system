package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.impl.OrderDAO;
import com.daidaisuki.inventory.db.TransactionManager;
import com.daidaisuki.inventory.enums.FulfillmentStatus;
import com.daidaisuki.inventory.exception.EntityNotFoundException;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.model.OrderItem;
import com.daidaisuki.inventory.model.dto.StockAllocation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
  private final TransactionManager transactionManager;
  private final OrderDAO orderDAO;
  private final InventoryService inventoryService;
  private final OrderItemService orderItemService;

  public OrderService(Connection connection) {
    transactionManager = new TransactionManager(connection);
    this.orderDAO = new OrderDAO(connection);
    this.inventoryService = new InventoryService(connection);
    this.orderItemService = new OrderItemService(connection);
  }

  public List<Order> listOrdersWithDetails() throws SQLException {
    List<Order> orders = orderDAO.findAll();
    for (Order order : orders) {
      List<OrderItem> items = orderItemService.listByOrderId(order.getId());
      order.setItems(items);
      order.updateTotals();
    }
    return orders;
  }

  public Order createOrder(Order uiOrder) throws SQLException, InsufficientStockException {
    uiOrder.updateTotals();
    return transactionManager.executeInTransaction(
        () -> {
          Order persistentOrder = orderDAO.save(uiOrder);
          persistentOrder.setCustomer(uiOrder.getCustomer());
          processOrderItemsInternal(persistentOrder);
          return persistentOrder;
        });
  }

  public Order updateOrder(Order uiOrder)
      throws SQLException, EntityNotFoundException, InsufficientStockException {
    return transactionManager.executeInTransaction(
        () -> {
          if (uiOrder.getId() <= 0) {
            throw new IllegalArgumentException("Order id: " + uiOrder.getId() + "is not valid.");
          }
          if (uiOrder.getFulfillmentStatus() == FulfillmentStatus.COMPLETED) {
            throw new EntityNotFoundException("Cannot update a completed order.");
          }
          revertInventoryForOrderInternal(uiOrder.getId());
          orderItemService.removeAllByOrderIdInternal(uiOrder.getId());
          processOrderItemsInternal(uiOrder);
          uiOrder.updateTotals();
          orderDAO.update(uiOrder);
          return orderDAO
              .findById(uiOrder.getId())
              .orElseThrow(() -> new EntityNotFoundException("Order not found after update."));
        });
  }

  public void removeOrder(int orderId) throws SQLException, InsufficientStockException {
    transactionManager.executeInTransaction(
        () -> {
          revertInventoryForOrderInternal(orderId);
          orderItemService.removeAllByOrderIdInternal(orderId);
          orderDAO.delete(orderId);
        });
  }

  public void revertInventoryForOrderInternal(int orderId) throws SQLException {
    List<OrderItem> itemstoRevert = orderItemService.listByOrderId(orderId);
    for (OrderItem item : itemstoRevert) {
      inventoryService.returnToInventoryInternal(
          item.getProductId(), orderId, item.getBatchId(), item.getQuantity());
    }
  }

  private void processOrderItemsInternal(Order uiOrder) throws SQLException {
    List<OrderItem> persistentItems = new ArrayList<>();
    for (OrderItem uiItem : uiOrder.getItems()) {
      List<StockAllocation> allocations =
          inventoryService.deductFromInventoryInternal(uiItem.getProductId(), uiItem.getQuantity());
      for (StockAllocation allocation : allocations) {
        OrderItem savedItem =
            orderItemService.createItemInternal(uiItem, uiOrder.getId(), allocation);
        savedItem.setProduct(uiItem.getProduct());
        persistentItems.add(savedItem);
      }
    }
    uiOrder.setItems(persistentItems);
  }

  public List<Order> getOrdersForCustomer(int customerId) throws SQLException {
    return orderDAO.findByCustomerId(customerId);
  }
}
