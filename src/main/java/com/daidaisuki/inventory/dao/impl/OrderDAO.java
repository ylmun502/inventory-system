package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.enums.FulfillmentStatus;
import com.daidaisuki.inventory.enums.FulfillmentType;
import com.daidaisuki.inventory.enums.PaymentMethod;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.Order;
import com.daidaisuki.inventory.model.dto.OrderStats;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class OrderDAO extends BaseDAO<Order> {
  public OrderDAO(Connection connection) {
    super(connection);
  }

  public List<Order> findAll() throws SQLException {
    String sql =
        """
        SELECT
          o.id AS o_id,
          o.customer_id,
          o.fulfillment_type,
          o.fulfillment_status,
          o.total_items,
          o.subtotal_cents,
          o.discount_amount_cents,
          o.tax_amount_cents,
          o.shipping_cost_cents,
          o.shipping_cost_actual_cents,
          o.final_amount_cents,
          o.payment_method,
          o.tracking_number,
          o.created_at,
          o.updated_at,
          o.is_deleted,
          c.id AS c_id,
          c.full_name AS c_full_name,
          c.phone_number AS c_phone_number,
          c.email AS c_email,
          c.address AS c_address,
          c.acquisition_source AS c_acquisition_source,
          c.created_at AS c_created_at,
          c.updated_at AS c_updated_at,
          c.is_deleted AS c_is_deleted
        FROM orders o LEFT JOIN customer_summary c On o.customer_id = c.id
        WHERE o.is_deleted = 0
        """;
    return query(sql, this::mapResultSetToOrder);
  }

  public Order save(Order order) throws SQLException {
    String sql =
        """
        INSERT INTO orders(
          customer_id,
          fulfillment_type,
          fulfillment_status,
          total_items,
          subtotal_cents,
          discount_amount_cents,
          tax_amount_cents,
          shipping_cost_cents,
          shipping_cost_actual_cents,
          final_amount_cents,
          payment_method,
          tracking_number,
          created_at,
          updated_at,
          is_deleted)
        VALUES(
          ?, ?, ?, ?, ?, ?, ?,
          ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    return insert(
        sql,
        (newId) ->
            new Order(
                newId,
                order.getCustomerId(),
                order.getFulfillmentType(),
                order.getFulfillmentStatus(),
                order.getTotalItems(),
                order.getSubtotalCents(),
                order.getDiscountAmountCents(),
                order.getTaxAmountCents(),
                order.getShippingCostCents(),
                order.getShippingCostActualCents(),
                order.getFinalAmountCents(),
                order.getPaymentMethod(),
                order.getTrackingNumber(),
                now,
                now,
                false),
        order.getCustomerId(),
        order.getFulfillmentType().name(),
        order.getFulfillmentStatus().name(),
        order.getTotalItems(),
        order.getSubtotalCents(),
        order.getDiscountAmountCents(),
        order.getTaxAmountCents(),
        order.getShippingCostCents(),
        order.getShippingCostActualCents(),
        order.getFinalAmountCents(),
        order.getPaymentMethod().name(),
        order.getTrackingNumber(),
        now,
        now,
        0);
  }

  public void update(Order order) throws SQLException {
    String sql =
        """
        UPDATE orders
        SET
          customer_id = ?,
          fulfillment_type = ?,
          fulfillment_status = ?,
          total_items = ?,
          subtotal_cents = ?,
          discount_amount_cents = ?,
          tax_amount_cents = ?,
          shipping_cost_cents = ?,
          shipping_cost_actual_cents = ?,
          final_amount_cents = ?,
          payment_method = ?,
          tracking_number = ?,
          updated_at = ?
        WHERE id = ?
        """;
    int affectedRows =
        update(
            sql,
            order.getCustomerId(),
            order.getFulfillmentType().name(),
            order.getFulfillmentStatus().name(),
            order.getTotalItems(),
            order.getSubtotalCents(),
            order.getDiscountAmountCents(),
            order.getTaxAmountCents(),
            order.getShippingCostCents(),
            order.getShippingCostActualCents(),
            order.getFinalAmountCents(),
            order.getPaymentMethod().name(),
            order.getTrackingNumber(),
            OffsetDateTime.now(ZoneOffset.UTC),
            order.getId());
    if (affectedRows == 0) {
      throw new SQLException("Updating order failed, no rows affected.");
    }
  }

  public Optional<Order> findById(int id) throws SQLException {
    String sql =
        """
        SELECT
          o.id AS o_id,
          o.customer_id,
          o.fulfillment_type,
          o.fulfillment_status,
          o.total_items,
          o.subtotal_cents,
          o.discount_amount_cents,
          o.tax_amount_cents,
          o.shipping_cost_cents,
          o.shipping_cost_actual_cents,
          o.final_amount_cents,
          o.payment_method,
          o.tracking_number,
          o.created_at,
          o.updated_at,
          o.is_deleted,
          c.id AS c_id,
          c.full_name AS c_full_name,
          c.phone_number AS c_phone_number,
          c.email AS c_email,
          c.address AS c_address,
          c.acquisition_source AS c_acquisition_source,
          c.created_at AS c_created_at,
          c.updated_at AS c_updated_at,
          c.is_deleted AS c_is_deleted
        FROM orders o
        LEFT JOIN customers c ON o.customer_id = c.id
        WHERE o.id = ?
        """;
    return queryForObject(sql, this::mapResultSetToOrder, id);
  }

  public void delete(int orderId) throws SQLException {
    String sql = "UPDATE orders SET is_deleted = 1, updated_at = ? WHERE id = ?";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), orderId);
  }

  public void restore(int orderId) throws SQLException {
    String sql = "UPDATE orders SET is_deleted = 0, updated_at = ? WHERE id = ? AND is_deleted = 1";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), orderId);
  }

  public Optional<OrderStats> getStatsForCustomer(int customerId) throws SQLException {
    String sql =
        """
        SELECT
          total_orders,
          total_spent,
          total_discount,
          last_order_date
        FROM customer_summary
        WHERE customer_id = ? AND is_deleted = 0
        """;
    return queryForObject(
        sql,
        rs ->
            new OrderStats(
                rs.getInt("total_orders"),
                rs.getLong("total_spent"),
                rs.getLong("total_discount"),
                rs.getObject("last_order_date", OffsetDateTime.class)),
        customerId);
  }

  public List<Order> findByCustomerId(int customerId) throws SQLException {
    String sql =
        """
        SELECT id, customer_id, fulfillment_status, created_at, final_amount_cents
        FROM orders
        WHERE customer_id = ? AND is_deleted = 0
        ORDER BY created_at DESC
        """;
    return query(sql, this::mapResultSetToOrder, customerId);
  }

  private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
    int id = rs.getInt("o_id");
    try {
      int customerId = rs.getInt("customer_id");
      FulfillmentType fulfillmentType =
          FulfillmentType.fromString(rs.getString("fulfillment_type"));
      FulfillmentStatus fulfillmentStatus =
          FulfillmentStatus.fromString(rs.getString("fulfillment_status"));
      int totalItems = rs.getInt("total_items");
      long subtotalCents = rs.getLong("subtotal_cents");
      long discountAmountCents = rs.getLong("discount_amount_cents");
      long taxAmountCents = rs.getLong("tax_amount_cents");
      long shippingCostCents = rs.getLong("shipping_cost_cents");
      long shippingCostActualCents = rs.getLong("shipping_cost_actual_cents");
      long finalAmountCents = rs.getLong("final_amount_cents");
      PaymentMethod paymentMethod = PaymentMethod.fromString(rs.getString("payment_method"));
      String trackingNumber = rs.getString("tracking_number");
      OffsetDateTime orderCreatedAt = rs.getObject("created_at", OffsetDateTime.class);
      OffsetDateTime orderUpdatedAt = rs.getObject("updated_at", OffsetDateTime.class);
      boolean orderIsDeleted = rs.getInt("is_deleted") == 1;
      String fullName = rs.getString("c_full_name");
      String phoneNumber = rs.getString("c_phone_number");
      String email = rs.getString("c_email");
      String address = rs.getString("c_address");
      String acquisitionSource = rs.getString("c_acquisition_source");
      OffsetDateTime customerCreatedAt = rs.getObject("c_created_at", OffsetDateTime.class);
      OffsetDateTime customerUpdatedAt = rs.getObject("c_updated_at", OffsetDateTime.class);
      boolean customerIsDeleted = rs.getInt("c_is_deleted") == 1;
      Customer customer =
          new Customer(
              customerId,
              fullName,
              phoneNumber,
              email,
              address,
              acquisitionSource,
              customerCreatedAt,
              customerUpdatedAt,
              customerIsDeleted);
      Order order =
          new Order(
              id,
              customerId,
              fulfillmentType,
              fulfillmentStatus,
              totalItems,
              subtotalCents,
              discountAmountCents,
              taxAmountCents,
              shippingCostCents,
              shippingCostActualCents,
              finalAmountCents,
              paymentMethod,
              trackingNumber,
              orderCreatedAt,
              orderUpdatedAt,
              orderIsDeleted);
      order.setCustomer(customer);
      return order;
    } catch (Exception e) {
      throw new SQLException("Mapping failed for Order ID: " + id, e);
    }
  }
}
