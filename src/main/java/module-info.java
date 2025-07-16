module com.daidaisuki.inventory {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    opens com.daidaisuki.inventory.controller.dialog to javafx.fxml;
    opens com.daidaisuki.inventory.controller.view to javafx.fxml;
    opens com.daidaisuki.inventory.base.controller to javafx.fxml;

    exports com.daidaisuki.inventory;
    exports com.daidaisuki.inventory.util;
    exports com.daidaisuki.inventory.enums;
    exports com.daidaisuki.inventory.interfaces;
}
