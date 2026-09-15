package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.enums.TransactionType;
<<<<<<< HEAD
import com.daidaisuki.inventory.model.InventoryTransaction;
=======
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.model.InventoryTransaction;
import com.daidaisuki.inventory.util.DatabaseUtils;
>>>>>>> origin/new
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class InventoryTransactionDAO extends BaseDAO<InventoryTransaction> {
  public InventoryTransactionDAO(Connection connection) {
    super(connection);
  }

<<<<<<< HEAD
  public List<InventoryTransaction> findAll() throws SQLException {
=======
  public List<InventoryTransaction> findAll() {
>>>>>>> origin/new
    String sql =
        """
        SELECT
          id,
          product_id,
          batch_id,
          user_id,
          reference_id,
          change_amount,
          transaction_type,
          reason_code,
          created_at,
          updated_at,
          is_deleted
        FROM inventory_transactions
        ORDER BY created_at ASC
        """;
    return query(sql, this::mapResultSetToTransaction);
  }

<<<<<<< HEAD
  public InventoryTransaction save(InventoryTransaction transaction) throws SQLException {
=======
  public InventoryTransaction save(InventoryTransaction transaction) {
>>>>>>> origin/new
    String sql =
        """
        INSERT INTO inventory_transactions(
          product_id,
          batch_id,
          user_id,
          reference_id,
          change_amount,
          transaction_type,
          reason_code,
          created_at,
          updated_at,
          is_deleted)
        VALUES (
          ?, ?, ?, ?, ?,
          ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
<<<<<<< HEAD
=======
    String nowString = now.toString();
>>>>>>> origin/new
    return insert(
        sql,
        (newId) ->
            new InventoryTransaction(
                newId,
                transaction.getProductId(),
                transaction.getBatchId(),
                transaction.getUserId(),
                transaction.getReferenceId(),
                transaction.getChangeAmount(),
                transaction.getTransactionType(),
                transaction.getReasonCode(),
                now,
                now,
                false),
        transaction.getProductId(),
        transaction.getBatchId(),
        transaction.getUserId(),
        transaction.getReferenceId(),
        transaction.getChangeAmount(),
        transaction.getTransactionType().name(),
        transaction.getReasonCode(),
<<<<<<< HEAD
        now,
        now,
        0);
  }

  public List<InventoryTransaction> findAllByProductId(int productId) throws SQLException {
=======
        nowString,
        nowString,
        0);
  }

  public List<InventoryTransaction> findAllByProductId(int productId) {
>>>>>>> origin/new
    String sql =
        """
        SELECT
          id,
          product_id,
          batch_id,
          user_id,
          reference_id,
          change_amount,
          transaction_type,
          reason_code,
          created_at,
          updated_at,
          is_deleted
        FROM inventory_transactions
        WHERE product_id = ?
        ORDER BY created_at DESC
        """;
    return query(sql, this::mapResultSetToTransaction, productId);
  }

<<<<<<< HEAD
  public List<InventoryTransaction> findAllByBatchId(int batchId) throws SQLException {
=======
  public List<InventoryTransaction> findAllByBatchId(int batchId) {
>>>>>>> origin/new
    String sql =
        """
        SELECT
          id,
          product_id,
          batch_id,
          user_id,
          reference_id,
          change_amount,
          transaction_type,
          reason_code,
          created_at,
          updated_at,
          is_deleted
        FROM inventory_transactions WHERE batch_id = ?
        ORDER BY created_at DESC
        """;
    return query(sql, this::mapResultSetToTransaction, batchId);
  }

<<<<<<< HEAD
  public List<InventoryTransaction> findAllByDateRange(OffsetDateTime start, OffsetDateTime end)
      throws SQLException {
=======
  public List<InventoryTransaction> findAllByDateRange(OffsetDateTime start, OffsetDateTime end) {
>>>>>>> origin/new
    String sql =
        """
        SELECT
          id,
          product_id,
          batch_id,
          user_id,
          reference_id,
          change_amount,
          transaction_type,
          reason_code,
          created_at,
          updated_at,
          is_deleted
        FROM inventory_transactions
        WHERE created_at BETWEEN ? AND ?
        ORDER BY created_at ASC
        """;
    return query(sql, this::mapResultSetToTransaction, start, end);
  }

<<<<<<< HEAD
  private InventoryTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
=======
  private InventoryTransaction mapResultSetToTransaction(ResultSet rs) {
    try {
      int id = rs.getInt("id");
>>>>>>> origin/new
      int productId = rs.getInt("product_id");
      int batchId = rs.getInt("batch_id");
      int userId = rs.getInt("user_id");
      int referenceId = rs.getInt("reference_id");
      int changeAmount = rs.getInt("change_amount");
      TransactionType transactionType = TransactionType.valueOf(rs.getString("transaction_type"));
      String reasonCode = rs.getString("reason_code");
<<<<<<< HEAD
      OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
      OffsetDateTime updatedAt = rs.getObject("updated_at", OffsetDateTime.class);
=======
      OffsetDateTime createdAt =
          DatabaseUtils.getOffsetDateTime(rs, "created_at", "InventoryTrabsaction ID: " + id);
      OffsetDateTime updatedAt =
          DatabaseUtils.getOffsetDateTime(rs, "updated_at", "InventoryTrabsaction ID: " + id);
>>>>>>> origin/new
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new InventoryTransaction(
          id,
          productId,
          batchId,
          userId,
          referenceId,
          changeAmount,
          transactionType,
          reasonCode,
          createdAt,
          updatedAt,
          isDeleted);
<<<<<<< HEAD
    } catch (Exception e) {
      throw new SQLException("Mapping failed for InventoryTransaction ID: " + id, e);
=======
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed", e);
>>>>>>> origin/new
    }
  }
}
