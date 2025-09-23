package com.daidaisuki.inventory;

import com.daidaisuki.inventory.db.DatabaseManager;
import com.daidaisuki.inventory.util.AlertHelper;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

  private static Scene scene;
  public static final double WIDTH_RATIO = 1.0;
  public static final double HEIGHT_RATIO = 1.0;
  public static final int MIN_WIDTH = 1200;
  public static final int MIN_HEIGHT = 800;

  @Override
  public void start(Stage stage) throws IOException {
    try {
      DatabaseManager.initializeDatabase();
    } catch (SQLException e) {
      e.printStackTrace();
      AlertHelper.showErrorAlert(
          stage, "Database Error", "Failed to initialize database", e.getMessage());
    }
    FXMLLoader fxmlLoader = loadFXMLLoader("main");
    Parent root = fxmlLoader.getRoot();
    Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
    double width = Math.max(visualBounds.getWidth() * WIDTH_RATIO, MIN_WIDTH);
    double height = Math.max(visualBounds.getHeight() * HEIGHT_RATIO, MIN_HEIGHT);
    scene = new Scene(root, width, height);
    scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
    stage.setScene(scene);
    stage
        .fullScreenProperty()
        .addListener(
            (obs, wasFull, isFull) -> {
              if (!isFull) {
                resizeStageToDynamicSize(stage);
              }
            });
    stage.show();
  }

  private static FXMLLoader loadFXMLLoader(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    fxmlLoader.load();
    return fxmlLoader;
  }

  private static void resizeStageToDynamicSize(Stage stage) {
    Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    stage.setWidth(Math.max(bounds.getWidth() * WIDTH_RATIO, MIN_WIDTH));
    stage.setHeight(Math.max(bounds.getHeight() * HEIGHT_RATIO, MIN_HEIGHT));
  }

  public static void main(String[] args) {
    launch();
  }
}
