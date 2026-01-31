package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.enums.TransactionType;
import com.daidaisuki.inventory.model.InventoryTransaction;
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

  public List<InventoryTransaction> findAll() throws SQLException {
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

  public InventoryTransaction save(InventoryTransaction transaction) throws SQLException {
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
        now,
        now,
        0);
  }

  public List<InventoryTransaction> findAllByProductId(int productId) throws SQLException {
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

  public List<InventoryTransaction> findAllByBatchId(int batchId) throws SQLException {
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

  public List<InventoryTransaction> findAllByDateRange(OffsetDateTime start, OffsetDateTime end)
      throws SQLException {
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

  private InventoryTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
      int productId = rs.getInt("product_id");
      int batchId = rs.getInt("batch_id");
      int userId = rs.getInt("user_id");
      int referenceId = rs.getInt("reference_id");
      int changeAmount = rs.getInt("change_amount");
      TransactionType transactionType = TransactionType.valueOf(rs.getString("transaction_type"));
      String reasonCode = rs.getString("reason_code");
      OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
      OffsetDateTime updatedAt = rs.getObject("updated_at", OffsetDateTime.class);
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
    } catch (Exception e) {
      throw new SQLException("Mapping failed for InventoryTransaction ID: " + id, e);
    }
  }
}
