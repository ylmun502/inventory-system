package com.daidaisuki.inventory.util;

import javafx.scene.Node;
import javafx.stage.Window;;

/**
 * Utility class for working with JavaFX {@link Window} objects and {@link Node}s.
 * <p>
 * This class provides methods to safely retrieve the {@link Window} from a {@link Node},
 * especially useful when handling events or dynamically generated components.
 * </p>
 */
public final class FxWindowUtils {
    
    private FxWindowUtils() {
        // Prevent instantiation
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the {@link Window} associated with the given {@link Node}.
     * <p>
     * If the node, its scene, or the window is {@code null}, this method will return {@code null}.
     * This is useful for checking the window context of a Node in JavaFX applications.
     * </p>
     *
     * @param node the JavaFX {@link Node} for which to find the window
     * @return the associated {@link Window}, or {@code null} if not available
     */
    public static Window getWindow(Node node) {
        if (node == null || node.getScene() == null) {
            return null;
        }
        return node.getScene().getWindow();
    }
}

