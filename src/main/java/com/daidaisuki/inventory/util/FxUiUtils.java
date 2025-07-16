package com.daidaisuki.inventory.util;

import javafx.scene.control.Button;

/**
 * Utility class for common JavaFX UI-related operations.
 * <p>
 * Provides convenience methods to handle UI interactions such as disabling buttons
 * during execution of a task.
 * </p>
 */
public class FxUiUtils {
    /**
     * Runs the specified operation while temporarily disabling the given buttons.
     * <p>
     * This method is useful to prevent repeated clicks or interactions while an action is being executed.
     * All buttons are re-enabled after the operation completes, regardless of success or failure.
     * <strong>Note:</strong> This runs the operation on the current thread (usually the JavaFX Application Thread),
     * so it is not suitable for long-running tasks.
     * </p>
     *
     * @param operation the task to execute
     * @param buttons   the buttons to disable during execution
     */
    public static void runWithButtonsDisabled(Runnable operation, Button... buttons) {
        for (Button btn : buttons) {
            btn.setDisable(true);
        }

        try {
            operation.run();
        } finally {
            for (Button btn : buttons) {
                btn.setDisable(false);
            }
        }
    }
}