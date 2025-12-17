package com.daidaisuki.inventory.util;

import com.daidaisuki.inventory.App;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

/**
 * Utility class for displaying styled JavaFX alert dialogs such as error, warning, information, and
 * confirmation dialogs.
 *
 * <p>This class simplifies alert creation and ensures consistent styling using a shared CSS
 * stylesheet ("styles.css"). It supports:
 *
 * <ul>
 *   <li>Error, warning, and information alerts
 *   <li>Confirmation dialogs with OK/Cancel options
 *   <li>Predefined alerts for common user actions or database errors
 * </ul>
 *
 * <p>All dialogs use the JavaFX {@link Alert} class and may be styled using the included
 * stylesheet. Dialogs can optionally specify an owner {@link Window}.
 *
 * <p><b>Threading Note:</b> Alerts must be created and shown on the JavaFX Application Thread. The
 * methods in this class handle thread checking internally using {@link
 * Platform#runLater(Runnable)}.
 */
public class AlertHelper {
  private static final String STYLESHEET = App.class.getResource("styles.css").toExternalForm();
  private static final int ALERT_PREF_HEIGHT = 350;
  private static final int ALERT_PREF_WIDTH = 400;

  private AlertHelper() {
    // Prevent instantiation
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Shows an error alert dialog.
   *
   * @param owner the owner window of the alert; can be {@code null}
   * @param title the alert title; defaults to an empty string if {@code null} or empty
   * @param header the alert header text; defaults to an empty string if {@code null} or empty
   * @param content the alert content text; defaults to an empty string if {@code null} or empty
   */
  public static void showErrorAlert(Window owner, String title, String header, String content) {
    showAlert(Alert.AlertType.ERROR, owner, title, header, content);
  }

  /**
   * Shows a warning alert dialog.
   *
   * @param owner the owner window of the alert; can be {@code null}
   * @param title the alert title; defaults to an empty string if {@code null} or empty
   * @param header the alert header text; defaults to an empty string if {@code null} or empty
   * @param content the alert content text; defaults to an empty string if {@code null} or empty
   */
  public static void showWarningAlert(Window owner, String title, String header, String content) {
    showAlert(Alert.AlertType.WARNING, owner, title, header, content);
  }

  /**
   * Shows an informational alert dialog.
   *
   * @param owner the owner window of the alert; can be {@code null}
   * @param title the alert title; defaults to an empty string if {@code null} or empty
   * @param header the alert header text; defaults to an empty string if {@code null} or empty
   * @param content the alert content text; defaults to an empty string if {@code null} or empty
   */
  public static void showInfoAlert(Window owner, String title, String header, String content) {
    showAlert(Alert.AlertType.INFORMATION, owner, title, header, content);
  }

  /**
   * Shows a confirmation dialog and returns {@code true} if the user clicks OK, {@code false}
   * otherwise.
   *
   * <p><b>Note:</b> Confirmation dialogs must be shown on the JavaFX Application Thread. If called
   * from a background thread, this method schedules the dialog using {@link
   * Platform#runLater(Runnable)}. In such cases, synchronization may be needed to retrieve the
   * result correctly.
   *
   * @param owner the owner window of the alert; can be {@code null}
   * @param title the alert title
   * @param header the alert header text
   * @param content the alert content text
   * @return {@code true} if the user clicked OK; {@code false} otherwise
   */
  public static boolean showConfirmationAlert(
      Window owner, String title, String header, String content) {
    final boolean[] userResponse = new boolean[1];
    Runnable showDialog =
        () -> {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          if (owner != null) {
            alert.initOwner(owner);
          }
          alert.setTitle(safeString(title));
          alert.setHeaderText(safeString(header));
          alert.setContentText(safeString(content));
          alert.getDialogPane().getStylesheets().add(STYLESHEET);
          alert.getDialogPane().getStyleClass().add("custom-alert");
          Optional<ButtonType> result = alert.showAndWait();
          userResponse[0] = result.isPresent() && result.get() == ButtonType.OK;
        };

    if (Platform.isFxApplicationThread()) {
      showDialog.run();
    } else {
      try {
        Platform.runLater(showDialog);
        // Note: showAndWait must be called on FX thread and blocks it,
        // so for confirmation dialogs called from background threads,
        // further synchronization might be needed.
      } catch (IllegalStateException e) {
        e.printStackTrace();
      }
    }
    return userResponse[0];
  }

  /**
   * Internal helper method to create and show an alert of the specified type.
   *
   * @param type the alert type (e.g. ERROR, WARNING)
   * @param owner the owner window; can be {@code null}
   * @param title the alert title
   * @param header the alert header text
   * @param content the alert content text
   */
  private static void showAlert(
      Alert.AlertType type, Window owner, String title, String header, String content) {
    Runnable showDialog =
        () -> {
          Alert alert = new Alert(type);
          if (owner != null) {
            alert.initOwner(owner);
          }
          alert.setTitle(safeString(title));
          alert.setHeaderText(safeString(header));
          alert.setContentText(safeString(content));
          alert.getDialogPane().getStylesheets().add(STYLESHEET);
          alert.getDialogPane().getStyleClass().add("custom-alert");
          alert.getDialogPane().setPrefHeight(ALERT_PREF_HEIGHT);
          alert.getDialogPane().setPrefWidth(ALERT_PREF_WIDTH);
          alert.showAndWait();
        };

    if (Platform.isFxApplicationThread()) {
      showDialog.run();
    } else {
      Platform.runLater(showDialog);
    }
  }

  /**
   * Returns a non-null, non-empty safe string.
   *
   * @param s the input string
   * @return an empty string if {@code s} is {@code null} or empty; otherwise, returns {@code s}
   */
  private static String safeString(String s) {
    return (s == null || s.isEmpty()) ? "" : s;
  }

  /**
   * Shows a warning alert indicating that a selection is required before performing the given
   * action.
   *
   * @param owner the owner window of the alert
   * @param action the action requiring a selection (e.g., "edit", "delete")
   */
  public static void showSelectionRequiredAlert(Window owner, String action) {
    showWarningAlert(owner, "Warning", null, "Please select a product to " + action + ".");
  }

  /**
   * Displays a styled error alert dialog related to a database operation.
   *
   * @param owner the owner window of the alert
   * @param context a description of the context in which the database error occurred
   * @param e the {@link SQLException} containing error details
   */
  public static void showDatabaseError(Window owner, String context, SQLException e) {
    e.printStackTrace();
    showErrorAlert(owner, "Database Error", context, e.getMessage());
  }
}
