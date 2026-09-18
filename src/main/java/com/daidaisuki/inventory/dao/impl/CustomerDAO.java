package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.util.DatabaseUtils;
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
  private static final String TABLE_NAME = "customers";
  private static final String BASE_SELECT_CUSTOMER_SUMMARY =
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
      """;
  private static final String ORDER_BY_NAME = " ORDER BY full_name ASC";

  public CustomerDAO(Connection connection) {
    super(connection);
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

  public void archive(int customerId) {
    this.setDeletionStatus(TABLE_NAME, customerId, true);
  }

  public void restore(int customerId) {
    this.setDeletionStatus(TABLE_NAME, customerId, false);
  }

  public void remove(int customerId) {
    this.deleteById(TABLE_NAME, customerId);
  }

  public Optional<Customer> findById(int id) {
    String sql = BASE_SELECT_CUSTOMER_SUMMARY + " WHERE id = ?";
    return queryForObject(sql, this::mapResultSetToCustomer, id);
  }

  public List<Customer> findAll() {
    String sql = BASE_SELECT_CUSTOMER_SUMMARY + ORDER_BY_NAME;
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
      BigDecimal totalSpent =
          DatabaseUtils.getBigDecimalFromCents(rs, "total_spent_cents", "Customer ID: " + id);
      BigDecimal totalDiscount =
          DatabaseUtils.getBigDecimalFromCents(rs, "total_discount_cents", "Customer ID: " + id);
      BigDecimal averageOrderValue =
          totalOrders > 0
              ? totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;
      OffsetDateTime lastOrderDate =
          DatabaseUtils.getOffsetDateTime(rs, "last_order_date", "Customer ID: " + id);
      OffsetDateTime createdAt =
          DatabaseUtils.getOffsetDateTime(rs, "created_at", "Customer ID: " + id);
      OffsetDateTime updatedAt =
          DatabaseUtils.getOffsetDateTime(rs, "updated_at", "Customer ID: " + id);
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
      throw new DataAccessException("Failed to map Customer", e);
    }
  }
}
