package com.daidaisuki.inventory;

import com.daidaisuki.inventory.util.AlertHelper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

import com.daidaisuki.inventory.db.DatabaseManager;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            DatabaseManager.initializeDatabase();
        } catch(SQLException e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert(
                stage,
                "Database Error",
                "Failed to initialize database",
                e.getMessage());
        }
        FXMLLoader fxmlLoader = loadFXMLLoader("main");
        Parent root = fxmlLoader.getRoot();
        scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.fullScreenProperty().addListener((obs, wasFull, isFull) -> {
            if (!isFull) {
                stage.setWidth(1200);
                stage.setHeight(800);
            }
        });
        stage.show();
    }

    private static FXMLLoader loadFXMLLoader(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.load();
        return fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }

}
