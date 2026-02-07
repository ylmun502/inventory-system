package com.daidaisuki.inventory.model.base;

import java.time.OffsetDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class BaseModel {
  public static final int NEW_ENTITY_ID = -1;
  protected final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper(this, "id", -1);
  protected final ReadOnlyObjectWrapper<OffsetDateTime> createdAt =
      new ReadOnlyObjectWrapper<>(this, "createdAt");
  protected final ObjectProperty<OffsetDateTime> updatedAt =
      new SimpleObjectProperty<>(this, "updatedAt");
  protected final BooleanProperty isDeleted = new SimpleBooleanProperty(this, "isDeleted", false);

  protected BaseModel(
      int id, OffsetDateTime createdAt, OffsetDateTime updatedAt, boolean isDeleted) {
    this.id.set(id);
    this.createdAt.set(createdAt);
    this.updatedAt.set(updatedAt);
    this.isDeleted.set(isDeleted);
  }

  public final int getId() {
    return this.id.get();
  }

  public final ReadOnlyIntegerProperty idProperty() {
    return this.id.getReadOnlyProperty();
  }

  public final OffsetDateTime getCreatedAt() {
    return this.createdAt.get();
  }

  public final ReadOnlyObjectProperty<OffsetDateTime> createdAtProperty() {
    return this.createdAt.getReadOnlyProperty();
  }

  public final OffsetDateTime getUpdatedAt() {
    return this.updatedAt.get();
  }

  public final void setUpdatedAt(OffsetDateTime time) {
    this.updatedAt.set(time);
  }

  public final ObjectProperty<OffsetDateTime> updatedAtProperty() {
    return this.updatedAt;
  }

  public final boolean isDeleted() {
    return this.isDeleted.get();
  }

  public final void setDeleted(boolean deleted) {
    this.isDeleted.set(deleted);
  }

  public final BooleanProperty isDeletedProperty() {
    return this.isDeleted;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    BaseModel other = (BaseModel) obj;
    if (this.getId() == -1 || other.getId() == -1) {
      return this == other;
    }
    return this.getId() == other.getId();
  }

  @Override
  public final int hashCode() {
    return this.getId() == -1 ? System.identityHashCode(this) : Integer.hashCode(this.getId());
  }
}
