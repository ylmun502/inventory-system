package com.daidaisuki.inventory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

  protected <R> Optional<R> queryForObject(String sql, RowMapper<R> mapper, Object... params)
      throws SQLException {
    List<R> results = query(sql, mapper, params);
    if (results.isEmpty()) {
      return Optional.empty();
    }
    if (results.size() > 1) {
      throw new SQLException("Expected one result, but found " + results.size());
    }
    return Optional.ofNullable(results.get(0));
  }

  protected <R> List<R> query(String sql, RowMapper<R> mapper, Object... params)
      throws SQLException {
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
    }
    return results;
  }

  protected T insert(String sql, GeneratedKeysMapper<T> mapper, Object... params)
      throws SQLException {
    try (PreparedStatement pStmt =
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      for (int i = 0; i < params.length; i++) {
        pStmt.setObject(i + 1, params[i]);
      }
      int affectedRows = pStmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Insert failed: No rows were affected by the operation.");
      }
      try (ResultSet generatedKeys = pStmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return mapper.mapKey(generatedKeys.getObject(1, Integer.class));
        } else {
          throw new SQLException(
              "Insert failed: Record was created but no generated ID was returned.");
        }
      }
    }
  }

  protected int update(String sql, Object... params) throws SQLException {
    try (PreparedStatement pStmt = connection.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        pStmt.setObject(i + 1, params[i]);
      }
      return pStmt.executeUpdate();
    }
  }
}
