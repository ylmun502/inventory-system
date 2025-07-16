package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.enums.View;
import com.daidaisuki.inventory.util.ViewLoader;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxWindowUtils;

import java.io.IOException;
import java.util.Stack;

import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML private VBox leftPane;
    @FXML private StackPane centerPane;

    @FXML private Button defaultButton;
    
    private final Stack<View> viewHistory = new Stack<>();
    private Button activeButton = null;

    @FXML
    public void initialize() {
            for (var node : leftPane.getChildren()) {
                if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(this::handleViewSwitch);
                button.setMaxWidth(Double.MAX_VALUE);
            }
        }
        try {
            setActiveButton(defaultButton);
            switchView(View.INVENTORY); // Load default view
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewSwitch(ActionEvent event) {
        Button btn = (Button) event.getSource();
        
        if (btn == activeButton) {
            // Ignore click if already active
            return;
        }

        setActiveButton(btn);

        // Switch the view
        String viewKey = (String) btn.getUserData();
        try {
            View view = View.valueOf(viewKey);
            switchView(view);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Invalid view: " + viewKey);
            e.printStackTrace();
            // Use AlertHelper, passing the current window as owner for proper modality
            AlertHelper.showErrorAlert(
                FxWindowUtils.getWindow((Node) event.getSource()),
                "View Error",
                "Unable to load view",
                "Something went wrong while trying to load the view.");
        }
    }

    private void switchView(View view) throws IOException {
        Parent newView = ViewLoader.loadParent(view);

        if (!centerPane.getChildren().isEmpty()) {
            // Save current view enum in history if you want to support goBack properly
            viewHistory.push(view);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(300), newView);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        centerPane.getChildren().setAll(newView);
        ft.play();
    }

    private void setActiveButton(Button btn) {
        // Remove 'active' style from previous button
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
            activeButton.setDisable(false); // enable previous button if you want
        }

        // Add 'active' style to the new button
        btn.getStyleClass().add("active");
        activeButton = btn;
    }

    public void goBack() throws IOException {
        if (!viewHistory.isEmpty()) {
            View previousView = viewHistory.pop();
            Parent prevView = ViewLoader.loadParent(previousView);
            centerPane.getChildren().setAll(prevView);
        }
    }
}
