package com.daidaisuki.inventory.db;

<<<<<<< HEAD
import com.daidaisuki.inventory.exception.InsufficientStockException;
=======
import com.daidaisuki.inventory.exception.DataAccessException;
>>>>>>> origin/new
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
  private final Connection connection;

  public TransactionManager(Connection connection) {
    this.connection = connection;
  }

  @FunctionalInterface
  public interface TransactionAction {
<<<<<<< HEAD
    void execute() throws SQLException, InsufficientStockException;
=======
    void execute();
>>>>>>> origin/new
  }

  @FunctionalInterface
  public interface TransactionCallable<T> {
<<<<<<< HEAD
    T execute() throws SQLException, InsufficientStockException;
  }

  public void executeInTransaction(TransactionAction action)
      throws SQLException, InsufficientStockException {
=======
    T execute();
  }

  public void executeInTransaction(TransactionAction action) {
>>>>>>> origin/new
    executeInTransaction(
        () -> {
          action.execute();
          return null;
        });
  }

<<<<<<< HEAD
  public <T> T executeInTransaction(TransactionCallable<T> action)
      throws SQLException, InsufficientStockException {
    boolean alreadyInTransaction = !connection.getAutoCommit();
=======
  public <T> T executeInTransaction(TransactionCallable<T> action) {
    boolean alreadyInTransaction = false;
    try {
      alreadyInTransaction = !connection.getAutoCommit();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to check transaction state", e);
    }
>>>>>>> origin/new
    if (alreadyInTransaction) {
      return action.execute();
    }
    try {
<<<<<<< HEAD
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
=======
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
>>>>>>> origin/new
}
