package com.daidaisuki.inventory.util;

import com.daidaisuki.inventory.interfaces.FxmlView;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
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

  public static Parent loadParent(FxmlView view, ServiceRegistry registry) throws IOException {
    FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(view.getFxml()));
    loader.setControllerFactory(type -> createControllerInstance(type, registry));
    return loader.load();
  }

  public static <C> C createControllerInstance(Class<C> controllerClass, Object... args) {
    try {
      Object[] finalArgs = args;
      if (args != null && args.length == 1 && args[0] instanceof Object[]) {
        finalArgs = (Object[]) args[0];
      }
      if (args == null || args.length == 0) {
        return controllerClass.getDeclaredConstructor().newInstance();
      }

      for (Constructor<?> constructor : controllerClass.getDeclaredConstructors()) {
        if (constructor.getParameterCount() == finalArgs.length) {
          boolean match = true;
          for (int i = 0; i < finalArgs.length; i++) {
            Class<?> paramType = constructor.getParameterTypes()[i];
            if (finalArgs[i] != null && !paramType.isAssignableFrom(finalArgs[i].getClass())) {
              match = false;
              break;
            }
          }
          if (match) {
            constructor.setAccessible(true);
            return controllerClass.cast(constructor.newInstance(finalArgs));
          }
        }
      }
      throw new NoSuchMethodException(
          "No suitable constructor found for "
              + controllerClass.getName()
              + " with args "
              + Arrays.toString(args));
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to instantiate controller: " + controllerClass.getName(), e);
    }
  }

  public static <T> Pair<Parent, T> loadViewWithControllerFactory(
      FxmlView view, Callback<Class<?>, Object> factory) throws Exception {
    FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(view.getFxml()));
    if (factory != null) {
      loader.setControllerFactory(factory);
    }
    Parent root = loader.load();
    return new Pair<>(root, loader.getController());
  }
}
