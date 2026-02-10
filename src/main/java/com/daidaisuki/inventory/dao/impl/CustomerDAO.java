package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.util.CurrencyUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class CustomerDAO extends BaseDAO<Customer> {
  public CustomerDAO(Connection connection) {
    super(connection);
  }

  public List<Customer> findAll() {
    String sql =
        """
        SELECT
          id,
          full_name,
          phone_number,
          email,
          address,
          acquisition_source,
          total_orders,
          total_spent_cents,
          total_discount_cents,
          last_order_date,
          created_at,
          updated_at,
          is_deleted
        FROM customer_summary
        WHERE is_deleted = 0
        ORDER BY full_name ASC
        """;
    return query(sql, this::mapResultSetToCustomer);
  }

  public Customer save(Customer customer) {
    String sql =
        """
        INSERT INTO customers(
          full_name,
          phone_number,
          email,
          address,
          acquisition_source,
          created_at,
          updated_at,
          is_deleted)
        VALUES(
          ?, ?, ?, ?,
          ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    String nowString = now.toString();
    return insert(
        sql,
        (newId) ->
            new Customer(
                newId,
                customer.getFullName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getAcquisitionSource(),
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                now,
                now,
                false),
        customer.getFullName(),
        customer.getPhoneNumber(),
        customer.getEmail(),
        customer.getAddress(),
        customer.getAcquisitionSource(),
        nowString,
        nowString,
        0);
  }

  public void update(Customer customer) {
    String sql =
        """
        UPDATE customers
        SET
          full_name = ?,
          phone_number = ?,
          email = ?,
          address = ?,
          acquisition_source = ?,
          updated_at = ?
        WHERE id = ?
        """;

    update(
        sql,
        customer.getFullName(),
        customer.getPhoneNumber(),
        customer.getEmail(),
        customer.getAddress(),
        customer.getAcquisitionSource(),
        OffsetDateTime.now(ZoneOffset.UTC),
        customer.getId());
  }

  public void delete(int customerId) {
    String sql = "UPDATE customers SET is_deleted = 1, updated_at = ? WHERE id = ?";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), customerId);
  }

  public void restore(int customerId) {
    String sql =
        "UPDATE customers SET is_deleted = 0, updated_at = ? WHERE id = ? AND is_deleted = 1";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), customerId);
  }

  public Optional<Customer> findById(int id) {
    String sql =
        """
        SELECT
          id,
          full_name,
          phone_number,
          email,
          address,
          acquisition_source,
          total_orders,
          total_spent_cents,
          total_discount_cents,
          last_order_date,
          created_at,
          updated_at,
          is_deleted
        FROM customer_summary
        WHERE id = ?
        """;
    return queryForObject(sql, this::mapResultSetToCustomer, id);
  }

  public List<Customer> findAllByName(String fullName) {
    String sql =
        """
        SELECT
          id,
          full_name,
          phone_number,
          email,
          address,
          acquisition_source,
          total_orders,
          total_spent_cents,
          total_discount_cents,
          last_order_date,
          created_at,
          updated_at,
          is_deleted
        FROM customer_summary
        WHERE full_name = ? AND is_deleted = 0
        """;
    return query(sql, this::mapResultSetToCustomer, fullName);
  }

  public List<Customer> findAllDeleted() {
    String sql =
        """
        SELECT
          id,
          full_name,
          phone_number,
          email,
          address,
          acquisition_source,
          total_orders,
          total_spent_cents,
          total_discount_cents,
          last_order_date,
          created_at,
          updated_at,
          is_deleted
        FROM customer_summary
        WHERE is_deleted = 1
        ORDER BY updated_at DESC
        """;
    return query(sql, this::mapResultSetToCustomer);
  }

  private Customer mapResultSetToCustomer(ResultSet rs) {
    try {
      int id = rs.getInt("id");
      String fullName = rs.getString("full_name");
      String phoneNumber = rs.getString("phone_number");
      String email = rs.getString("email");
      String address = rs.getString("address");
      String acquisitionSource = rs.getString("acquisition_source");
      int totalOrders = rs.getInt("total_orders");
      BigDecimal totalSpent = CurrencyUtil.longToBigDecimal(rs.getLong("total_spent_cents"));
      BigDecimal totalDiscount = CurrencyUtil.longToBigDecimal(rs.getLong("total_discount_cents"));
      BigDecimal averageOrderValue =
          totalOrders > 0
              ? totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;
      OffsetDateTime lastOrderDate = rs.getObject("last_order_date", OffsetDateTime.class);
      OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
      OffsetDateTime updatedAt = rs.getObject("updated_at", OffsetDateTime.class);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new Customer(
          id,
          fullName,
          phoneNumber,
          email,
          address,
          acquisitionSource,
          totalOrders,
          totalSpent,
          totalDiscount,
          averageOrderValue,
          lastOrderDate,
          createdAt,
          updatedAt,
          isDeleted);
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed", e);
    }
  }
}
