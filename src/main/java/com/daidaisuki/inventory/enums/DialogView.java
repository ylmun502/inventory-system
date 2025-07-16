package com.daidaisuki.inventory.enums;

import com.daidaisuki.inventory.interfaces.FxmlView;

public enum DialogView implements FxmlView {
    PRODUCT_DIALOG("productDialog.fxml");

    private final String fxml;

    DialogView(String fxml) {
        this.fxml = fxml;
    }

    @Override
    public String getFxml() {
        return "/com/daidaisuki/inventory/" + fxml;
    }
}
