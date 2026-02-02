package com.daidaisuki.inventory.util;

import com.daidaisuki.inventory.controller.view.InventoryController;
import com.daidaisuki.inventory.controller.view.MainController;
import com.daidaisuki.inventory.interfaces.FxmlView;
import java.io.IOException;
import java.sql.Connection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Utility class for loading JavaFX views using {@link FXMLLoader}.
 *
 * <p>This class provides convenience methods to load FXML files, retrieve their root nodes,
 * controllers, or both as a pair. It is designed to work with {@link FxmlView}, which is assumed to
 * provide the FXML path.
 */
public final class ViewLoader {
  private ViewLoader() {
    // Prevent instantiation
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Creates a new {@link FXMLLoader} for the specified FXML view.
   *
   * @param view the {@link FxmlView} enum or object that provides the FXML file path
   * @return a new {@link FXMLLoader} instance
   */
  public static FXMLLoader loadFxml(FxmlView view) {
    return new FXMLLoader(ViewLoader.class.getResource(view.getFxml()));
  }

  /**
   * Loads the root {@link Parent} node from the specified FXML view.
   *
   * @param view the {@link FxmlView} to load
   * @return the root {@link Parent} node
   * @throws IOException if loading the FXML file fails
   */
  public static Parent loadParent(FxmlView view, Connection connection) throws IOException {
    FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(view.getFxml()));
    loader.setControllerFactory(
        type -> {
          try {
            if (type == InventoryController.class) {
              return new InventoryController(connection);
            }
            if (type == MainController.class) {
              return new MainController(connection);
            }
            return type.getDeclaredConstructor().newInstance();
          } catch (Exception e) {
            throw new RuntimeException("Dependency Injection failed for: " + type.getName(), e);
          }
        });
    return loader.load();
  }

  /**
   * Loads the controller associated with the specified FXML view.
   *
   * @param view the {@link FxmlView} to load
   * @param <T> the expected controller type
   * @return the controller instance
   * @throws IOException if loading the FXML file fails
   */
  public static <T> T loadController(FxmlView view) throws IOException {
    FXMLLoader loader = loadFxml(view);
    loader.load();
    return loader.getController();
  }

  /**
   * Loads both the root {@link Parent} and the controller from the specified FXML view.
   *
   * @param view the {@link FxmlView} to load
   * @param <T> the expected controller type
   * @return a {@link Pair} containing the root node and its controller
   * @throws IOException if loading the FXML file fails
   */
  public static <T> Pair<Parent, T> loadViewAndController(FxmlView view) throws IOException {
    return loadViewWithControllerFactory(view, null);
  }

  public static <T> Pair<Parent, T> loadViewWithControllerFactory(
      FxmlView view, Callback<Class<?>, Object> factory) throws IOException {
    FXMLLoader loader = loadFxml(view);
    if (factory != null) {
      loader.setControllerFactory(factory);
    }
    Parent root = loader.load();
    return new Pair<>(root, loader.getController());
  }
}
