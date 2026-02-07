package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.model.StockBatch;
import com.daidaisuki.inventory.util.CurrencyUtil;
import com.daidaisuki.inventory.util.DatabaseUtils;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class StockBatchDAO extends BaseDAO<StockBatch> {
  public StockBatchDAO(Connection connection) {
    super(connection);
  }

  public StockBatch save(StockBatch batch) throws SQLException {
    String sql =
        """
        INSERT INTO stock_batches(
          product_id,
          supplier_id,
          batch_code,
          expiry_date,
          quantity_received,
          quantity_remaining,
          unit_cost_cents,
          landed_cost_cents,
          created_at,
          updated_at,
          is_deleted)
        VALUES (
        ?, ?, ?, ?, ?,
        ?, ?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    String nowString = now.toString();
    return insert(
        sql,
        (newId) ->
            StockBatch.forDatabase(
                newId,
                batch.getProductId(),
                batch.getSupplierId(),
                batch.getBatchCode(),
                batch.getExpiryDate(),
                batch.getQuantityReceived(),
                batch.getQuantityRemaining(),
                batch.getUnitCost(),
                batch.getLandedCost(),
                now,
                now,
                false),
        batch.getProductId(),
        batch.getSupplierId(),
        batch.getBatchCode(),
        batch.getExpiryDate(),
        batch.getQuantityReceived(),
        batch.getQuantityRemaining(),
        CurrencyUtil.bigDecimalToLong(batch.getUnitCost()),
        CurrencyUtil.bigDecimalToLong(batch.getLandedCost()),
        nowString,
        nowString,
        0);
  }

  public void updateRemainingStock(int batchId, int newQuantity) throws SQLException {
    String sql = "UPDATE stock_batches SET quantity_remaining = ?, updated_at = ? WHERE id = ?";
    int affectedRow = update(sql, newQuantity, OffsetDateTime.now(ZoneOffset.UTC), batchId);
    if (affectedRow == 0) {
      throw new SQLException("Batch not found with ID: " + batchId);
    }
  }

  public List<StockBatch> findAllByProductId(int productId) throws SQLException {
    String sql =
        """
        SELECT
          id,
          product_id,
          supplier_id,
          batch_code,
          expiry_date,
          quantity_received,
          quantity_remaining,
          unit_cost_cents,
          landed_cost_cents,
          created_at,
          updated_at,
          is_deleted
        FROM stock_batches
        WHERE product_id = ?
        """;
    return query(sql, this::mapResultSetToStockBatch, productId);
  }

  public List<StockBatch> findAllAvailableByProductId(int productId) throws SQLException {
    String sql =
        """
        SELECT
          id,
          product_id,
          supplier_id,
          batch_code,
          expiry_date,
          quantity_received,
          quantity_remaining,
          unit_cost_cents,
          landed_cost_cents,
          created_at,
          updated_at,
          is_deleted
        FROM stock_batches
        WHERE product_id = ? AND quantity_remaining > 0
        ORDER BY created_at ASC
        """;
    return query(sql, this::mapResultSetToStockBatch, productId);
  }

  public boolean updateStockTotal(int batchId, int changeAmount) throws SQLException {
    String sql =
        """
        UPDATE stock_batches
        SET quantity_remaining = quantity_remaining + ?, updated_at = ?
        WHERE id = ? AND quantity_remaining + ? >= 0
        """;
    return update(sql, changeAmount, OffsetDateTime.now(ZoneOffset.UTC), batchId, changeAmount) > 0;
  }

  public Optional<StockBatch> findOldestAvailableBatch(int productId) throws SQLException {
    String sql =
        """
        SELECT
          id,
          product_id,
          supplier_id,
          batch_code,
          expiry_date,
          quantity_received,
          quantity_remaining,
          unit_cost_cents,
          landed_cost_cents,
          created_at,
          updated_at,
          is_deleted
        FROM stock_batches
        WHERE product_id = ? AND quantity_remaining > 0
        ORDER BY created_at ASC
        LIMIT 1
        """;
    return queryForObject(sql, this::mapResultSetToStockBatch, productId);
  }

  private StockBatch mapResultSetToStockBatch(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    // Remove this try block before production
    try {
      int productId = rs.getInt("product_id");
      int supplierId = rs.getInt("supplier_id");
      String batchCode = rs.getString("batch_code");
      OffsetDateTime expiryDate =
          DatabaseUtils.getOffsetDateTime(rs, "expiry_date", "StockBatch ID: " + id);
      int quantityReceived = rs.getInt("quantity_received");
      int quantityRemaining = rs.getInt("quantity_remaining");
      BigDecimal unitCost =
          DatabaseUtils.getBigDecimalFromCents(rs, "unit_cost_cents", "StockBatch ID: " + id);
      BigDecimal landedCost =
          DatabaseUtils.getBigDecimalFromCents(rs, "landed_cost_cents", "StockBatch ID: " + id);
      OffsetDateTime createdAt =
          DatabaseUtils.getOffsetDateTime(rs, "created_at", "StockBatch ID: " + id);
      OffsetDateTime updatedAt =
          DatabaseUtils.getOffsetDateTime(rs, "updated_at", "StockBatch ID: " + id);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return StockBatch.forDatabase(
          id,
          productId,
          supplierId,
          batchCode,
          expiryDate,
          quantityReceived,
          quantityRemaining,
          unitCost,
          landedCost,
          createdAt,
          updatedAt,
          isDeleted);
    } catch (Exception e) {
      e.printStackTrace();
      throw new SQLException("Mapping failed for StockBatch ID: " + id, e);
    }
  }
}
