package com.daidaisuki.inventory.service;

import com.daidaisuki.inventory.dao.OrderItemDAO;
import com.daidaisuki.inventory.dao.ProductDAO;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.model.OrderItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderItemService {
  private final OrderItemDAO orderItemDAO;
  private final ProductDAO productDAO;

  public OrderItemService(Connection connection) {
    this.orderItemDAO = new OrderItemDAO(connection);
    this.productDAO = new ProductDAO(connection);
  }

  public void addOrderItem(Order order, OrderItem item) throws SQLException {
    item.setOrderId(order.getId());
    orderItemDAO.addOrderItem(item);
    finalizeOrderItem(order, item);
  }

  public void updateOrderItem(Order order, OrderItem item) throws SQLException {
    item.setOrderId(order.getId());
    if (item.getId() <= 0) {
      orderItemDAO.addOrderItem(item);
    } else {
      orderItemDAO.updateOrderItem(item);
    }
    finalizeOrderItem(order, item);
  }

  public void deleteOrderItem(int orderItemId) throws SQLException {
    orderItemDAO.deleteOrderItem(orderItemId);
  }

  public List<OrderItem> getItemsByOrder(Order order) throws SQLException {
    return orderItemDAO.getItemsByOrderId(order.getId());
  }

  public void deleteItemsByOrderId(int orderId) throws SQLException {
    orderItemDAO.deleteByOrderId(orderId);
  }

  private void finalizeOrderItem(Order order, OrderItem item) throws SQLException {
    if (order.getCompleted()) {
      productDAO.decrementStock(item.getProductId(), item.getQuantity());
      if (item.getProduct() != null) {
        item.setCostAtSale(item.getProduct().getPrice());
      }
    }
  }
}
