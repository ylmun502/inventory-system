package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.OrderDAO;
import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import com.daidaisuki.inventory.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderService {
  private Connection connection;
  private final OrderDAO orderDAO;
  private final ProductDAO productDAO;
  private final OrderItemService orderItemService;

  public OrderService() {
    this(getConnectionSafely());
  }

  public OrderService(Connection connection) {
    this.connection = connection;
    this.orderDAO = new OrderDAO(connection);
    this.productDAO = new ProductDAO(connection);
    this.orderItemService = new OrderItemService(connection);
  }

  private static Connection getConnectionSafely() {
    try {
      return DatabaseManager.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to initialize OrderService", e);
    }
  }

  public List<Order> getAllOrdersWithDetail() throws SQLException {
    List<Order> orders = orderDAO.getAllOrders();
    for (Order order : orders) {
      List<OrderItem> items = orderItemService.getItemsByOrder(order);
      order.setItems(items);
      order.recalculateTotals();
    }
    return orders;
  }

  public void createOrderWithItems(Order order) throws SQLException, InsufficientStockException {
    executeInTransaction(
        () -> {
          validateStockIfCompleted(order);
          orderDAO.addOrder(order);
          for (OrderItem item : order.getItems()) {
            orderItemService.addOrderItem(order, item);
          }
        });
  }

  public void updateOrder(Order order) throws SQLException, InsufficientStockException {
    executeInTransaction(
        () -> {
          validateStockIfCompleted(order);
          orderDAO.updateOrder(order);
          List<OrderItem> existingItems = orderItemService.getItemsByOrder(order);
          Map<Integer, OrderItem> existingMap =
              existingItems.stream().collect(Collectors.toMap(OrderItem::getId, i -> i));
          for (OrderItem item : order.getItems()) {
            if (order.getId() == 0) {
              orderItemService.addOrderItem(order, item);
            } else if (existingMap.containsKey(item.getId())) {
              orderItemService.updateOrderItem(order, item);
              existingMap.remove(item.getId());
            } else {
              throw new SQLException("Order item with id " + item.getId() + " not found in DB");
            }
          }
          for (OrderItem removedItem : existingMap.values()) {
            orderItemService.deleteOrderItem(removedItem.getId());
          }
        });
  }

  public void deleteOrder(int orderId) throws SQLException, InsufficientStockException {
    executeInTransaction(
        () -> {
          orderItemService.deleteItemsByOrderId(orderId);
          orderDAO.deleteOrder(orderId);
        });
  }

  private void validateStockForOrder(Order order) throws SQLException, InsufficientStockException {
    for (OrderItem item : order.getItems()) {
      Product product = productDAO.getById(item.getProductId());
      if (product == null) {
        throw new SQLException("Product not found: id=" + item.getProductId());
      }
      if (product.getStock() < item.getQuantity()) {
        throw new InsufficientStockException(
            "Insufficient stock for product: " + product.getName());
      }
    }
  }

  private void validateStockIfCompleted(Order order)
      throws SQLException, InsufficientStockException {
    if (order.getCompleted()) {
      validateStockForOrder(order);
    }
  }

  private void executeInTransaction(TransactionAction action)
      throws SQLException, InsufficientStockException {
    try {
      connection.setAutoCommit(false);
      action.execute();
      connection.commit();
    } catch (SQLException | InsufficientStockException e) {
      connection.rollback();
      throw e;
    } finally {
      connection.setAutoCommit(true);
    }
  }

  @FunctionalInterface
  private interface TransactionAction {
    void execute() throws SQLException, InsufficientStockException;
  }
}
