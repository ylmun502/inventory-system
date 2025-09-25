package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.*;
import com.daidaisuki.inventory.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
  private Connection connection;
  private final OrderDAO orderDAO;
  private final OrderItemDAO orderItemDAO;
  private final ProductDAO productDAO;

  public OrderService(Connection connection) {
    this.connection = connection;
    this.orderDAO = new OrderDAO(connection);
    this.orderItemDAO = new OrderItemDAO(connection);
    this.productDAO = new ProductDAO(connection);
  }

  public List<Order> getAllOrdersWithDetail() throws SQLException {
    List<Order> orders = orderDAO.getAllOrders();
    for (Order order : orders) {
      List<OrderItem> items = orderItemDAO.getItemsByOrderId(order.getId());
      order.setItems(items);
      order.recalculateTotals();
    }
    return orders;
  }

  public void createOrderWithItems(Order order) throws SQLException {
    try {
      connection.setAutoCommit(false);
      for (OrderItem item : order.getItems()) {
        Product product = productDAO.getById(item.getProductId());
        if (product == null) {
          throw new SQLException("Product not found: id=" + item.getProductId());
        }
        if (product.getStock() < item.getQuantity()) {
          throw new IllegalArgumentException(
              "Insufficient stock for product: " + product.getName());
        }
      }
      orderDAO.addOrder(order);
      for (OrderItem item : order.getItems()) {
        item.setOrderId(order.getId());
        orderItemDAO.addOrderItem(item);
        productDAO.decrementStock(item.getProductId(), item.getQuantity());
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
      orderDAO.updateOrder(order);
      List<OrderItem> existingItems = orderItemDAO.getItemsByOrderId(order.getId());
      for (OrderItem existingItem : existingItems) {
        if (!order.getItems().contains(existingItem)) {
          orderItemDAO.deleteOrderItem(existingItem.getId());
        }
      }
      for (OrderItem item : order.getItems()) {
        item.setOrderId(order.getId());
        if (item.getId() <= 0) {
          orderItemDAO.addOrderItem(item);
          productDAO.decrementStock(item.getProductId(), item.getQuantity());
        } else {
          orderItemDAO.updateOrderItem(item);
        }
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
      orderItemDAO.deleteByOrderId(orderId);
      orderDAO.deleteOrder(orderId);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    } finally {
      connection.setAutoCommit(true);
    }
  }
}
