package com.daidaisuki.inventory.controller.view;

import com.daidaisuki.inventory.enums.View;
import com.daidaisuki.inventory.serviceregistry.ServiceRegistry;
import com.daidaisuki.inventory.util.AlertHelper;
import com.daidaisuki.inventory.util.FxWindowUtils;
import com.daidaisuki.inventory.util.ViewLoader;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController {
  @FXML private VBox leftPane;
  @FXML private StackPane centerPane;

  @FXML private Button defaultButton;

  private final ServiceRegistry registry;
  private Button activeButton = null;

  public MainController(ServiceRegistry registry) {
    this.registry = registry;
  }

  @FXML
  public void initialize() {
    this.leftPane.setPrefWidth(150);
    for (var node : this.leftPane.getChildren()) {
      if (node instanceof Button button) {
        button.setOnAction(this::handleViewSwitch);
        button.setMaxWidth(Double.MAX_VALUE);
      }
    }
    if (this.defaultButton != null) {
      setActiveButton(this.defaultButton);
    }
    String viewKey = (String) defaultButton.getUserData();
    try {
      switchView(View.valueOf(viewKey)); // Load default view
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleViewSwitch(ActionEvent event) {
    Button button = (Button) event.getSource();

    if (button == this.activeButton) {
      // Ignore click if already active
      return;
    }

    setActiveButton(button);

    // Switch the view
    String viewKey = (String) button.getUserData();
    try {
      View view = View.valueOf(viewKey);
      switchView(view);
    } catch (Exception e) {
      // Use AlertHelper, passing the current window as owner for proper modality
      AlertHelper.showErrorAlert(
          FxWindowUtils.getWindow((Node) event.getSource()),
          "Navigation Error",
          "Unable to load " + viewKey,
          e.getMessage());
    }
  }

  private void switchView(View view) throws IOException {
    Parent newView = ViewLoader.loadParent(view, this.registry);

    FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), newView);
    fadeTransition.setFromValue(0.5);
    fadeTransition.setToValue(1.0);

    centerPane.getChildren().setAll(newView);
    fadeTransition.play();
  }

  private void setActiveButton(Button button) {
    // Remove 'active' style from previous button
    if (this.activeButton != null) {
      activeButton.getStyleClass().remove("active");
    }

    // Add 'active' style to the new button
    button.getStyleClass().add("active");
    this.activeButton = button;
  }
}
