package com.daidaisuki.inventory.dao.impl;

import com.daidaisuki.inventory.dao.BaseDAO;
<<<<<<< HEAD
=======
import com.daidaisuki.inventory.exception.DataAccessException;
>>>>>>> origin/new
import com.daidaisuki.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class SupplierDAO extends BaseDAO<Supplier> {
<<<<<<< HEAD
=======
  private static final String TABLE_NAME = "suppliers";

>>>>>>> origin/new
  public SupplierDAO(Connection connection) {
    super(connection);
  }

<<<<<<< HEAD
  public List<Supplier> findAll() throws SQLException {
=======
  public List<Supplier> findAll() {
>>>>>>> origin/new
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

<<<<<<< HEAD
  public Supplier save(Supplier supplier) throws SQLException {
=======
  public Supplier save(Supplier supplier) {
>>>>>>> origin/new
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
<<<<<<< HEAD
=======
    String nowString = now.toString();
>>>>>>> origin/new
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
<<<<<<< HEAD
        now,
        now,
        0);
  }

  public void update(Supplier supplier) throws SQLException {
=======
        nowString,
        nowString,
        0);
  }

  public void update(Supplier supplier) {
>>>>>>> origin/new
    String sql =
        """
          UPDATE suppliers
          SET
<<<<<<< HEAD
            name,
            email,
            phone,
            address,
            updated_at
          WHERE id = ?
        )
        """;
    int affectedRows =
        update(
            sql,
            supplier.getName(),
            supplier.getEmail(),
            supplier.getPhone(),
            supplier.getAddress(),
            OffsetDateTime.now(ZoneOffset.UTC),
            supplier.getId());
    if (affectedRows == 0) {
      throw new SQLException("Updating product failed, no rows affected.");
    }
  }

  public void delete(int supplierId) throws SQLException {
    String sql =
        "UPDATE suppliers SET is_deleted = 0, updated_at = ? WHERE id = ? AND is_deleted = 1";
    update(sql, OffsetDateTime.now(ZoneOffset.UTC), supplierId);
  }

  public Optional<Supplier> findById(int id) throws SQLException {
=======
            name = ?,
            email = ?,
            phone = ?,
            address = ?,
            updated_at = ?
          WHERE id = ?
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
>>>>>>> origin/new
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

<<<<<<< HEAD
  public boolean existsByShortCode(String shortCode) throws SQLException {
=======
  public boolean existsByShortCode(String shortCode) {
>>>>>>> origin/new
    String sql = "SELECT COUNT(*) FROM suppliers WHERE short_code = ? AND is_deleted = 0";
    return queryForObject(sql, rs -> rs.getInt(1) > 0, shortCode).orElse(false);
  }

<<<<<<< HEAD
  private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    try {
=======
  private Supplier mapResultSetToSupplier(ResultSet rs) {
    try {
      int id = rs.getInt("id");
>>>>>>> origin/new
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
<<<<<<< HEAD
    } catch (Exception e) {
      throw new SQLException("Mapping failed for Supplier ID: " + id, e);
=======
    } catch (SQLException e) {
      throw new DataAccessException("Mapping failed.", e);
>>>>>>> origin/new
    }
  }
}
