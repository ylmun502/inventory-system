package com.daidaisuki.inventory.db;

import com.daidaisuki.inventory.exception.DataAccessException;
import com.daidaisuki.inventory.exception.InsufficientStockException;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
  private final Connection connection;

  public TransactionManager(Connection connection) {
    this.connection = connection;
  }

  @FunctionalInterface
  public interface TransactionAction {
    void execute() throws InsufficientStockException;
  }

  @FunctionalInterface
  public interface TransactionCallable<T> {
    T execute() throws InsufficientStockException;
  }

  public void executeInTransaction(TransactionAction action) throws InsufficientStockException {
    executeInTransaction(
        () -> {
          action.execute();
          return null;
        });
  }

  public <T> T executeInTransaction(TransactionCallable<T> action)
      throws InsufficientStockException {
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
    } catch (InsufficientStockException e) {
      this.safeRollback();
      throw e;
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
