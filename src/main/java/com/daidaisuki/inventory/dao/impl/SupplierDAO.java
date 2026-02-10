package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
import com.daidaisuki.inventory.exception.DataAccessException;
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
        WHERE is_deleted = 0
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
            name,
            email,
            phone,
            address,
            updated_at
          WHERE id = ?
        )
        """;

    update(
        sql,
        supplier.getName(),
        supplier.getEmail(),
        supplier.getPhone(),
        supplier.getAddress(),
        OffsetDateTime.now(ZoneOffset.UTC),
        supplier.getId());
  }

  public void delete(int supplierId) {
    String sql =
        "UPDATE suppliers SET is_deleted = 0, updated_at = ? WHERE id = ? AND is_deleted = 1";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), supplierId);
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
    String sql = "SELECT COUNT(*) FROM suppliers WHERE short_code = ? AND is_deleted = 0";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, shortCode).orElse(false);
  }

  private Supplier mapResultSetToSupplier(ResultSet rs) {
    try {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      String shortCode = rs.getString("short_code");
      String email = rs.getString("email");
      String phone = rs.getString("phone");
      String address = rs.getString("address");
      String createdAtString = rs.getString("created_at");
      String updatedAtString = rs.getString("updated_at");
      OffsetDateTime createdAt = OffsetDateTime.parse(createdAtString);
      OffsetDateTime updatedAt = OffsetDateTime.parse(updatedAtString);
      boolean isDeleted = rs.getInt("is_deleted") == 1;
      return new Supplier(
          id, name, shortCode, email, phone, address, createdAt, updatedAt, isDeleted);
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed.", e);
    }
  }
}
