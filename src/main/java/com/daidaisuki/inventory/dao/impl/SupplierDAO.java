package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.util.DatabaseUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class SupplierDAO extends BaseDAO<Supplier> {
  private static final String TABLE_NAME = "suppliers";

  public SupplierDAO(Connection connection) {
    super(connection);
  }

  public List<Supplier> findAll() {
    String sql =
        """
        SELECT
          id,
          name,
          short_code,
          email,
          phone,
          address,
          created_at,
          updated_at,
          is_deleted
        FROM suppliers
        ORDER BY name ASC
        """;
    return query(sql, this::mapResultSetToSupplier);
  }

  public Supplier save(Supplier supplier) {
    String sql =
        """
        INSERT INTO suppliers(
          name,
          short_code,
          email,
          phone,
          address,
          created_at,
          updated_at,
          is_deleted)
        VALUES(?, ?, ?, ?,
               ?, ?, ?, ?)
        """;
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    String nowString = now.toString();
    return insert(
        sql,
        (newId) ->
            new Supplier(
                newId,
                supplier.getName(),
                supplier.getShortCode(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                now,
                now,
                false),
        supplier.getName(),
        supplier.getShortCode(),
        supplier.getEmail(),
        supplier.getPhone(),
        supplier.getAddress(),
        nowString,
        nowString,
        0);
  }

  public void update(Supplier supplier) {
    String sql =
        """
          UPDATE suppliers
          SET
            name = ?,
            short_code = ?,
            email = ?,
            phone = ?,
            address = ?,
            updated_at = ?
          WHERE id = ?
        """;

    update(
        sql,
        supplier.getName(),
        supplier.getShortCode(),
        supplier.getEmail(),
        supplier.getPhone(),
        supplier.getAddress(),
        OffsetDateTime.now(ZoneOffset.UTC),
        supplier.getId());
  }

  public void archive(int supplierId) {
    this.setDeletionStatus(TABLE_NAME, supplierId, true);
  }

  public void restore(int supplierId) {
    this.setDeletionStatus(TABLE_NAME, supplierId, false);
  }

  public void remove(int supplierId) {
    this.deleteById(TABLE_NAME, supplierId);
  }

  public Optional<Supplier> findById(int id) {
    String sql =
        """
        SELECT
          id,
          name,
          short_code,
          email,
          phone,
          address,
          created_at,
          updated_at,
          is_deleted
        FROM suppliers
        WHERE id = ? AND is_deleted = 0
        """;
    return queryForObject(sql, this::mapResultSetToSupplier, id);
  }

  public boolean existsByShortCode(String shortCode) {
    String sql = "SELECT COUNT(*) FROM suppliers WHERE short_code = ?";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, shortCode).orElse(false);
  }

  public boolean existsByShortCodeExcludingId(String shortCode, int supplierId) {
    String sql = "SELECT COUNT(*) FROM suppliers WHERE short_code = ? and id <> ?";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, shortCode, supplierId).orElse(false);
  }

  private Supplier mapResultSetToSupplier(ResultSet rs) {
    try {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      String shortCode = rs.getString("short_code");
      String email = rs.getString("email");
      String phone = rs.getString("phone");
      String address = rs.getString("address");
      OffsetDateTime createdAt =
          DatabaseUtils.getOffsetDateTime(rs, "created_at", "Supplier ID: " + id);
      OffsetDateTime updatedAt =
          DatabaseUtils.getOffsetDateTime(rs, "updated_at", "Supplier ID: " + id);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new Supplier(
          id, name, shortCode, email, phone, address, createdAt, updatedAt, isDeleted);
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed.", e);
    }
  }
}
