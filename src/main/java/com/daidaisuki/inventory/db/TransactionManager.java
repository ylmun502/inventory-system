package com.daidaisuki.inventory.db;

import com.daidaisuki.inventory.exception.DataAccessException;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
  private final Connection connection;

  public TransactionManager(Connection connection) {
    this.connection = connection;
  }

  @FunctionalInterface
  public interface TransactionAction {
    void execute();
  }

  @FunctionalInterface
  public interface TransactionCallable<T> {
    T execute();
  }

  public void executeInTransaction(TransactionAction action) {
    executeInTransaction(
        () -> {
          action.execute();
          return null;
        });
  }

  public <T> T executeInTransaction(TransactionCallable<T> action) {
    boolean alreadyInTransaction = false;
    try {
      alreadyInTransaction = !connection.getAutoCommit();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to check transaction state", e);
    }
    if (alreadyInTransaction) {
      return action.execute();
    }
    try {
      this.safeSetAutoCommit(false);
      T result = action.execute();
      this.safeCommit();
      return result;
    } catch (RuntimeException e) {
      this.safeRollback();
      throw e;
    } finally {
      if (!alreadyInTransaction) {
        this.safeSetAutoCommit(true);
      }
    }
  }

  private void safeSetAutoCommit(boolean autoCommit) {
    try {
      connection.setAutoCommit(autoCommit);
    } catch (SQLException e) {
      throw new DataAccessException("Failed to set auto-commit.", e);
    }
  }

  private void safeCommit() {
    try {
      connection.commit();
    } catch (SQLException e) {
      throw new DataAccessException("Commit failed.", e);
    }
  }

  private void safeRollback() {
    try {
      connection.rollback();
    } catch (SQLException e) {
      throw new DataAccessException("Rollback failed.", e);
    }
  }
}
