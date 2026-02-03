package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.interfaces.Displayable;
import com.daidaisuki.inventory.model.base.BaseModel;
import java.time.OffsetDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Supplier extends BaseModel implements Displayable {
  private final StringProperty name = new SimpleStringProperty(this, "name", "");
  private final StringProperty shortCode = new SimpleStringProperty(this, "shortCode", "");
  private final StringProperty email = new SimpleStringProperty(this, "email", "");
  private final StringProperty phone = new SimpleStringProperty(this, "phone", "");
  private final StringProperty address = new SimpleStringProperty(this, "address", "");

  public Supplier() {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
  }

  public Supplier(
      int id,
      String name,
      String shortCode,
      String email,
      String phone,
      String address,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.name.set(name);
    this.shortCode.set(shortCode);
    this.email.set(email);
    this.phone.set(phone);
    this.address.set(address);
  }

  public String getName() {
    return this.name.get();
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public StringProperty namProperty() {
    return this.name;
  }

  public String getShortCode() {
    return this.shortCode.get();
  }

  public void setShortCode(String shortCode) {
    this.shortCode.set(shortCode);
  }

  public StringProperty shortCodeProperty() {
    return this.shortCode;
  }

  public String getEmail() {
    return this.email.get();
  }

  public void setEmail(String email) {
    this.email.set(email);
  }

  public StringProperty emailProperty() {
    return this.email;
  }

  public String getPhone() {
    return this.phone.get();
  }

  public void setPhone(String phone) {
    this.phone.set(phone);
  }

  public StringProperty phoneProperty() {
    return this.phone;
  }

  public String getAddress() {
    return this.address.get();
  }

  public void setAddress(String address) {
    this.address.set(address);
  }

  public StringProperty addressProperty() {
    return this.address;
  }

  @Override
  public String getDisplayName() {
    return this.name.get() + " (" + this.shortCode.get() + ")";
  }
}
