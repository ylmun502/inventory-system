package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class SupplierDAO extends BaseDAO<Supplier> {
  public SupplierDAO(Connection connection) {
    super(connection);
  }

  public Supplier save(Supplier supplier) throws SQLException {
    String sql =
        """
        INSERT INTO suppliers(
          name,
          short_code,
          created_at,
          updated_at,
          is_deleted)
        VALUES(?, ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    return insert(
        sql,
        (newId) ->
            new Supplier(newId, supplier.getName(), supplier.getShortCode(), now, now, false),
        supplier.getName(),
        supplier.getShortCode(),
        now,
        now,
        0);
  }

  public List<Supplier> findAll() throws SQLException {
    String sql =
        """
        SELECT
          id,
          name,
          short_code,
          created_at,
          updated_at,
          is_deleted
        FROM suppliers
        WHERE is_deleted = 0
        ORDER BY name ASC
        """;
    return query(sql, this::mapResultSetToSupplier);
  }

  public Optional<Supplier> findById(int id) throws SQLException {
    String sql =
        """
        SELECT
          id,
          name,
          short_code,
          created_at,
          updated_at,
          is_deleted
        FROM suppliers
        WHERE id = ? AND is_deleted = 0
        """;
    return queryForObject(sql, this::mapResultSetToSupplier, id);
  }

  public boolean existsByShortCode(String shortCode) throws SQLException {
    String sql = "SELECT COUNT(*) FROM suppliers WHERE short_code = ? AND is_deleted = 0";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, shortCode).orElse(false);
  }

  private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
      String name = rs.getString("name");
      String shortCode = rs.getString("short_code");
      OffsetDateTime createdAt = rs.getObject("createdAt", OffsetDateTime.class);
      OffsetDateTime updatedAt = rs.getObject("updatedAt", OffsetDateTime.class);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new Supplier(id, name, shortCode, createdAt, updatedAt, isDeleted);
    } catch (Exception e) {
      throw new SQLException("Mapping failed for Supplier ID: " + id, e);
    }
  }
}
