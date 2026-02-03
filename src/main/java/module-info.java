module com.daidaisuki.inventory {
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive javafx.graphics;
  requires javafx.base;
  requires transitive java.sql;
  requires org.xerial.sqlitejdbc;

  opens com.daidaisuki.inventory.controller.dialog to
      javafx.fxml;
  opens com.daidaisuki.inventory.controller.view to
      javafx.fxml;
  opens com.daidaisuki.inventory.base.controller to
      javafx.fxml;
  opens com.daidaisuki.inventory.model to
      javafx.base;

  exports com.daidaisuki.inventory;
  exports com.daidaisuki.inventory.enums;
  exports com.daidaisuki.inventory.interfaces;
  exports com.daidaisuki.inventory.model;
  exports com.daidaisuki.inventory.model.dto;
  exports com.daidaisuki.inventory.service;
  exports com.daidaisuki.inventory.serviceregistry;
  exports com.daidaisuki.inventory.util;
}
