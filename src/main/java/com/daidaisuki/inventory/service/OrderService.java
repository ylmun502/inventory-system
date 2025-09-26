package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.OrderDAO;
import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
  private Connection connection;
  private final OrderDAO orderDAO;
  private final ProductDAO productDAO;
  private final OrderItemService orderItemService;

  public OrderService(Connection connection) {
    this.connection = connection;
    this.orderDAO = new OrderDAO(connection);
    this.productDAO = new ProductDAO(connection);
    this.orderItemService = new OrderItemService(connection);
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

  public void createOrderWithItems(Order order) throws SQLException {
    try {
      connection.setAutoCommit(false);
      validateStockIfCompleted(order);
      orderDAO.addOrder(order);
      for (OrderItem item : order.getItems()) {
        orderItemService.addOrderItem(order, item);
      }
      connection.commit();
    } catch (SQLException | RuntimeException e) {
      connection.rollback();
      throw e;
    } finally {
      connection.setAutoCommit(true);
    }
  }

  public void updateOrder(Order order) throws SQLException {
    try {
      connection.setAutoCommit(false);
      validateStockIfCompleted(order);
      orderDAO.updateOrder(order);
      List<OrderItem> existingItems = orderItemService.getItemsByOrder(order);
      for (OrderItem existingItem : existingItems) {
        if (!order.getItems().contains(existingItem)) {
          orderItemService.deleteOrderItem(existingItem.getId());
        }
      }
      for (OrderItem item : order.getItems()) {
        orderItemService.updateOrderItem(order, item);
      }
      connection.commit();
    } catch (SQLException | RuntimeException e) {
      connection.rollback();
      throw e;
    } finally {
      connection.setAutoCommit(true);
    }
  }

  public void deleteOrder(int orderId) throws SQLException {
    try {
      connection.setAutoCommit(false);
      orderItemService.deleteItemsByOrderId(orderId);
      orderDAO.deleteOrder(orderId);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    } finally {
      connection.setAutoCommit(true);
    }
  }

  private void validateStockForOrder(Order order) throws SQLException {
    for (OrderItem item : order.getItems()) {
      Product product = productDAO.getById(item.getProductId());
      if (product == null) {
        throw new SQLException("Product not found: id=" + item.getProductId());
      }
      if (product.getStock() < item.getQuantity()) {
        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
      }
    }
  }

  private void validateStockIfCompleted(Order order) throws SQLException {
    if (order.getCompleted()) {
      validateStockForOrder(order);
    }
  }
}
