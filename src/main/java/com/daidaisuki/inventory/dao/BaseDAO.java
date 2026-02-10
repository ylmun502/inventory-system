package com.daidaisuki.inventory.dao;

import com.daidaisuki.inventory.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseDAO<T> {
  protected final Connection connection;

  protected BaseDAO(Connection connection) {
    this.connection = connection;
  }

  @FunctionalInterface
  protected interface RowMapper<R> {
    R mapRow(ResultSet rs) throws SQLException;
  }

  @FunctionalInterface
  protected interface GeneratedKeysMapper<T> {
    T mapKey(int id) throws SQLException;
  }

  protected <R> Optional<R> queryForObject(String sql, RowMapper<R> mapper, Object... params) {
    List<R> results = query(sql, mapper, params);
    if (results.isEmpty()) {
      return Optional.empty();
    }
    if (results.size() > 1) {
      throw new DataAccessException("Expected one result but found multiple.");
    }
    return Optional.ofNullable(results.get(0));
  }

  protected <R> List<R> query(String sql, RowMapper<R> mapper, Object... params) {
    List<R> results = new ArrayList<>();
    try (PreparedStatement pStmt = connection.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        pStmt.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = pStmt.executeQuery()) {
        while (rs.next()) {
          results.add(mapper.mapRow(rs));
        }
      }
      return results;
    } catch (SQLException e) {
      throw new DataAccessException("Database query failed.", e);
    }
  }

  protected T insert(String sql, GeneratedKeysMapper<T> mapper, Object... params) {
    try (PreparedStatement pStmt =
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      for (int i = 0; i < params.length; i++) {
        pStmt.setObject(i + 1, params[i]);
      }
      int affectedRows = pStmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DataAccessException("Insert failed.");
      }
      try (ResultSet generatedKeys = pStmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return mapper.mapKey(generatedKeys.getInt(1));
        }
        throw new DataAccessException("Insert succeeded but no ID returned.");
      }
    } catch (SQLException e) {
      throw new DataAccessException("Database insert failed.", e);
    }
  }

  protected void update(String sql, Object... params) {
    int affectedRows = updateReturningAffectedRows(sql, params);
    if (affectedRows == 0) {
      throw new DataAccessException("Update failed, no rows affected.");
    }
  }

  protected int updateReturningAffectedRows(String sql, Object... params) {
    try (PreparedStatement pStmt = connection.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        pStmt.setObject(i + 1, params[i]);
      }
      return pStmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Database update failed.", e);
    }
  }

  protected void setDeletionStatus(String tableName, int id, boolean isDeleted) {
    String sql =
        String.format("UPDATE %s SET is_deleted = ?, updated_at = ? WHERE id = ?", tableName);
    update(sql, isDeleted ? 1 : 0, OffsetDateTime.now(ZoneOffset.UTC).toString(), id);
  }
}
