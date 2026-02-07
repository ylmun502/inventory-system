package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.model.OrderItem;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class OrderItemDAO extends BaseDAO<OrderItem> {
  public OrderItemDAO(Connection connection) {
    super(connection);
  }

  public OrderItem save(OrderItem item) throws SQLException {
    String sql =
        """
        INSERT INTO order_items(
          order_id,
          product_id,
          batch_id,
          quantity,
          unit_price_at_sale_cents,
          unit_cost_at_sale_cents,
          created_at,
          updated_at,
          is_deleted)
        VALUES(
          ?, ?, ?, ?,
          ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    String nowString = now.toString();
    return insert(
        sql,
        (newId) ->
            new OrderItem(
                newId,
                item.getOrderId(),
                item.getProductId(),
                item.getBatchId(),
                item.getQuantity(),
                item.getUnitPriceAtSaleCents(),
                item.getUnitCostAtSaleCents(),
                now,
                now,
                false),
        item.getOrderId(),
        item.getProductId(),
        item.getBatchId(),
        item.getQuantity(),
        item.getUnitPriceAtSaleCents(),
        item.getUnitCostAtSaleCents(),
        nowString,
        nowString,
        0);
  }

  public void update(OrderItem item) throws SQLException {
    String sql =
        """
        UPDATE order_items
        SET
          quantity = ?,
          unit_price_at_sale_cents = ?,
          unit_cost_at_sale_cents = ?,
          updated_at = ?
        WHERE id = ?
        """;
    update(
        sql,
        item.getQuantity(),
        item.getUnitPriceAtSaleCents(),
        item.getUnitCostAtSaleCents(),
        OffsetDateTime.now(ZoneOffset.UTC),
        item.getId());
  }

  public void delete(int orderItemId) throws SQLException {
    String sql =
        "UPDATE order_items SET is_deleted = 1, updated_at = ? WHERE id = ? AND is_deleted = 0";
    int affectedRows = update(sql, OffsetDateTime.now(ZoneOffset.UTC), orderItemId);
    if (affectedRows == 0) {
      throw new SQLException("Deleting order item failed, no rows affected.");
    }
  }

  public List<OrderItem> findAllByOrderId(int orderId) throws SQLException {
    String sql =
        """
        SELECT
          id,
          order_id,
          product_id,
          batch_id,
          quantity,
          unit_price_at_sale_cents,
          unit_cost_at_sale_cents,
          created_at,
          updated_at,
          is_deleted
        FROM order_items
        WHERE order_id = ? AND is_deleted = 0
        ORDER BY id ASC
        """;
    return query(sql, this::mapResultSetToOrderItem, orderId);
  }

  public void deleteAllByOrderId(int orderId) throws SQLException {
    String sql =
        """
        UPDATE order_items
        SET
          is_deleted = 1, updated_at = ?
        WHERE order_id = ? AND is_deleted = 0
        """;
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), orderId);
  }

  private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
      int orderId = rs.getInt("order_id");
      int productId = rs.getInt("product_id");
      int batchId = rs.getInt("batch_id");
      int quantity = rs.getInt("quantity");
      long unitPriceAtSaleCents = rs.getLong("unit_price_at_sale_cents");
      long unitCostAtSaleCents = rs.getLong("unit_cost_at_sale_cents");
      OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
      OffsetDateTime updatedAt = rs.getObject("updated_at", OffsetDateTime.class);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new OrderItem(
          id,
          orderId,
          productId,
          batchId,
          quantity,
          unitPriceAtSaleCents,
          unitCostAtSaleCents,
          createdAt,
          updatedAt,
          isDeleted);
    } catch (Exception e) {
      throw new SQLException("Mapping failed for OrderItem ID: " + id, e);
    }
  }
}
