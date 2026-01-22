package com.daidaisuki.inventory.db;

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
    void execute() throws SQLException, InsufficientStockException;
  }

  @FunctionalInterface
  public interface TransactionCallable<T> {
    T execute() throws SQLException, InsufficientStockException;
  }

  public void executeInTransaction(TransactionAction action)
      throws SQLException, InsufficientStockException {
    executeInTransaction(
        () -> {
          action.execute();
          return null;
        });
  }

  public <T> T executeInTransaction(TransactionCallable<T> action)
      throws SQLException, InsufficientStockException {
    boolean alreadyInTransaction = !connection.getAutoCommit();
    if (alreadyInTransaction) {
      return action.execute();
    }
    try {
      connection.setAutoCommit(false);
      T result = action.execute();
      connection.commit();
      return result;
    } catch (SQLException | InsufficientStockException e) {
      connection.rollback();
      throw e;
    } finally {
      if (!alreadyInTransaction) {
        connection.setAutoCommit(true);
      }
    }
  }
}
