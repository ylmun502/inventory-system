package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.model.OrderItem;
import com.daidaisuki.inventory.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
  private final Connection connection;

  public OrderItemDAO(Connection connection) {
    this.connection = connection;
  }

  public void addOrderItem(OrderItem item) throws SQLException {
    String sql =
        "INSERT INTO order_items(order_id, product_id, quantity, unit_price, cost_at_sale) VALUES"
            + " (?, ?, ?, ?, ?)";
    try (PreparedStatement stmt =
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setInt(1, item.getOrderId());
      int productId = item.getProductId();
      if (productId <= 0 && item.getProduct() != null) {
        Product product = item.getProduct();
        productId = product != null ? product.getId() : -1;
      }
      stmt.setInt(2, productId);
      stmt.setInt(3, item.getQuantity());
      stmt.setDouble(4, item.getUnitPrice());
      stmt.setDouble(5, item.getCostAtSale());
      int affectedRow = stmt.executeUpdate();
      if (affectedRow == 0) {
        throw new SQLException("Inserting order item failed, no rows affected.");
      }
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          item.setId(generatedKeys.getInt(1));
        }
      }
    }
  }

  public void updateOrderItem(OrderItem item) throws SQLException {
    String sql =
        "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, unit_price = ?,"
            + " cost_at_sale = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, item.getOrderId());
      stmt.setInt(2, item.getProductId());
      stmt.setInt(3, item.getQuantity());
      stmt.setDouble(4, item.getUnitPrice());
      stmt.setDouble(5, item.getCostAtSale());
      stmt.setInt(6, item.getId());

      int affectedRow = stmt.executeUpdate();
      if (affectedRow == 0) {
        throw new SQLException("Updating order item failed, no rows affected.");
      }
    }
  }

  public void deleteOrderItem(int orderItemId) throws SQLException {
    String sql = "DELETE from order_items WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, orderItemId);
      int affectedRow = stmt.executeUpdate();
      if (affectedRow == 0) {
        throw new SQLException("Deleting order item failed, no rows affected.");
      }
    }
  }

  public List<OrderItem> getItemsByOrderId(int orderId) throws SQLException {
    List<OrderItem> items = new ArrayList<>();
    String sql = "SELECT * FROM order_items WHERE order_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, orderId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          OrderItem item =
              new OrderItem(
                  rs.getInt("id"),
                  rs.getInt("order_id"),
                  rs.getInt("product_id"),
                  rs.getInt("quantity"),
                  rs.getDouble("unit_price"),
                  rs.getDouble("cost_at_sale"));
          items.add(item);
        }
      }
    }
    return items;
  }
}
