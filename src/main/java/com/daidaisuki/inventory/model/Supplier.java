package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.interfaces.Displayable;
import com.daidaisuki.inventory.model.base.BaseModel;
import java.time.OffsetDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Supplier extends BaseModel implements Displayable {
  private final StringProperty name = new SimpleStringProperty(this, "name", "");
  private final StringProperty shortCode = new SimpleStringProperty(this, "shortCode", "");

  public Supplier(
      int id,
      String name,
      String shortCode,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.name.set(name);
    this.shortCode.set(shortCode);
  }

  public String getName() {
    return this.name.get();
  }

  public StringProperty namProperty() {
    return this.name;
  }

  public String getShortCode() {
    return this.shortCode.get();
  }

  public StringProperty shortCodeProperty() {
    return this.shortCode;
  }

  @Override
  public String getDisplayName() {
    return this.name.get() + " (" + this.shortCode.get() + ")";
  }
}
