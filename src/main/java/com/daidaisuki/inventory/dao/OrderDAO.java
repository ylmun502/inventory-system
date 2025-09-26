package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.model.OrderItem;
import com.daidaisuki.inventory.model.dto.OrderStats;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
  private final Connection connection;
  private final OrderItemDAO orderItemDAO;

  public OrderDAO(Connection connection) {
    this.connection = connection;
    this.orderItemDAO = new OrderItemDAO(connection);
  }

  public List<Order> getAllOrders() throws SQLException {
    List<Order> orders = new ArrayList<>();
    String sql =
        "SELECT o.*, "
            + "c.id AS c_id, "
            + "c.name AS c_name, "
            + "c.phone_number AS c_phone_number, "
            + "c.email AS c_email, "
            + "c.address AS c_address, "
            + "c.platform AS c_platform "
            + "FROM orders o LEFT JOIN customers c On o.customer_id = c.id";
    try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        String orderDateStr = rs.getString("order_date");
        LocalDate orderDate = null;
        if (orderDateStr != null && !orderDateStr.isEmpty()) {
          orderDate = LocalDate.parse(orderDateStr);
        }
        Customer customer =
            new Customer(
                rs.getInt("c_id"),
                rs.getString("c_name"),
                rs.getString("c_phone_number"),
                rs.getString("c_email"),
                rs.getString("c_address"),
                rs.getString("c_platform"));
        Order order =
            new Order(
                rs.getInt("id"),
                orderDate,
                rs.getInt("total_items"),
                rs.getDouble("total_amount"),
                rs.getDouble("discount_amount"),
                rs.getString("payment_method"));
        order.setCustomer(customer);
        List<OrderItem> items = orderItemDAO.getItemsByOrderId(order.getId());
        order.setItems(items);
        orders.add(order);
      }
    }
    return orders;
  }

  public void addOrder(Order order) throws SQLException {
    String sql =
        "INSERT INTO orders(customer_id, order_date, total_items, total_amount, discount_amount,"
            + " payment_method, completed) VALUES(?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt =
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setInt(1, order.getCustomer().getId());
      setDateStringOrNull(stmt, 2, order.getDate());
      stmt.setInt(3, order.getTotalItems());
      stmt.setDouble(4, order.getTotalAmount());
      stmt.setDouble(5, order.getDiscountAmount());
      stmt.setString(6, order.getPaymentMethod());
      stmt.setBoolean(7, order.getCompleted());
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Creating order failed, no row affected.");
      }
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          order.setId(generatedKeys.getInt(1));
        } else {
          throw new SQLException("Creating order failed, no ID obtained.");
        }
      }
    }
  }

  public void updateOrder(Order order) throws SQLException {
    String sql =
        "UPDATE orders SET customer_id = ?, order_date = ?, total_items = ?, total_amount = ?,"
            + " discount_amount = ?, payment_method = ?, completed = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, order.getCustomer().getId());
      setDateStringOrNull(stmt, 2, order.getDate());
      stmt.setInt(3, order.getTotalItems());
      stmt.setDouble(4, order.getTotalAmount());
      stmt.setDouble(5, order.getDiscountAmount());
      stmt.setString(6, order.getPaymentMethod());
      stmt.setBoolean(7, order.getCompleted());
      stmt.setInt(8, order.getId());
      stmt.executeUpdate();
    }
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
      } else {
        orderItemDAO.updateOrderItem(item);
      }
    }
  }

  public void deleteOrder(int orderId) throws SQLException {
    String sql = "DELETE FROM orders WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, orderId);
      stmt.executeUpdate();
    }
  }

  public static void setDateStringOrNull(PreparedStatement stmt, int index, LocalDate date)
      throws SQLException {
    if (date != null) {
      stmt.setString(index, date.toString());
    } else {
      stmt.setNull(index, java.sql.Types.VARCHAR);
    }
  }

  public OrderStats getStatsForCustomer(int customerId) throws SQLException {
    String sql =
        "SELECT COUNT(*) AS total_orders, "
            + "SUM(total_amount) AS total_spent, "
            + "SUM(discount_amount) AS total_discount "
            + "FROM orders WHERE customer_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, customerId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int totalOrders = rs.getInt("total_orders");
        double totalSpent = rs.getDouble("total_spent");
        double totalDiscount = rs.getDouble("total_discount");
        return new OrderStats(totalOrders, totalSpent, totalDiscount);
      } else {
        return new OrderStats(0, 0.0, 0.0);
      }
    }
  }
}
