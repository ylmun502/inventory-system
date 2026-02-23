package com.daidaisuki.inventory.model;

import com.daidaisuki.inventory.enums.FulfillmentStatus;
import com.daidaisuki.inventory.enums.FulfillmentType;
import com.daidaisuki.inventory.enums.PaymentMethod;
import com.daidaisuki.inventory.model.base.BaseModel;
import java.time.OffsetDateTime;
import java.util.List;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Order extends BaseModel {
  private final ReadOnlyIntegerWrapper customerId =
      new ReadOnlyIntegerWrapper(this, "customerId", -1);

  private final ObjectProperty<FulfillmentType> fulfillmentType =
      new SimpleObjectProperty<>(this, "fulfillmentType", FulfillmentType.SHIPPING);
  private final ObjectProperty<FulfillmentStatus> fulfillmentStatus =
      new SimpleObjectProperty<>(this, "fulfillmentStatus", FulfillmentStatus.PENDING);
  private final ReadOnlyIntegerWrapper totalItems =
      new ReadOnlyIntegerWrapper(this, "totalItems", 0);
  private final ReadOnlyLongWrapper subtotalCents =
      new ReadOnlyLongWrapper(this, "subtotalCents", 0L);
  private final LongProperty taxAmountCents = new SimpleLongProperty(this, "taxAmountCents", 0L);
  private final LongProperty discountAmountCents =
      new SimpleLongProperty(this, "discountAmountCents", 0L);
  private final LongProperty shippingCostCents =
      new SimpleLongProperty(this, "shippingCostCents", 0L);
  private final LongProperty shippingCostActualCents =
      new SimpleLongProperty(this, "shippingCostActualCents", 0L);
  private final ReadOnlyLongWrapper finalAmountCents =
      new ReadOnlyLongWrapper(this, "finalAmountCents", 0L);
  private final ObjectProperty<PaymentMethod> paymentMethod =
      new SimpleObjectProperty<>(this, "paymentMethod", PaymentMethod.ZELLE);
  private final StringProperty trackingNumber =
      new SimpleStringProperty(this, "trackingNumber", "");

  private final ObjectProperty<Customer> customer =
      new SimpleObjectProperty<Customer>(this, "customer");

  private final ObservableList<OrderItem> items = FXCollections.observableArrayList();
  private final ChangeListener<Number> totalsUpdater = (obs, oldVal, newVal) -> updateTotals();
  private final WeakChangeListener<Number> weakTotalsUpdater =
      new WeakChangeListener<>(totalsUpdater);

  public Order() {
    super(-1, OffsetDateTime.now(), OffsetDateTime.now(), false);
    initListeners();
  }

  public Order(
      int id,
      int customerId,
      FulfillmentType fulfillmentType,
      FulfillmentStatus fulfillmentStatus,
      int totalItems,
      long subtotalCents,
      long discountAmountCents,
      long taxAmountCents,
      long shippingCostCents,
      long shippingCostActualCents,
      long finalAmountCents,
      PaymentMethod paymentMethod,
      String trackingNumber,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      boolean deleted) {
    super(id, createdAt, updatedAt, deleted);
    this.customerId.set(customerId);
    this.fulfillmentType.set(fulfillmentType);
    this.fulfillmentStatus.set(fulfillmentStatus);
    this.totalItems.set(totalItems);
    this.subtotalCents.set(subtotalCents);
    this.taxAmountCents.set(taxAmountCents);
    this.discountAmountCents.set(discountAmountCents);
    this.shippingCostCents.set(shippingCostCents);
    this.shippingCostActualCents.set(shippingCostActualCents);
    this.finalAmountCents.set(finalAmountCents);
    this.paymentMethod.set(paymentMethod);
    this.trackingNumber.set(trackingNumber);
    initListeners();
  }

  private void initListeners() {
    items.addListener(
        (ListChangeListener<OrderItem>)
            change -> {
              while (change.next()) {
                if (change.wasAdded()) {
                  for (OrderItem item : change.getAddedSubList()) {
                    item.quantityProperty().addListener(weakTotalsUpdater);
                    item.subtotalProperty().addListener(weakTotalsUpdater);
                  }
                }
                if (change.wasRemoved()) {
                  for (OrderItem item : change.getRemoved()) {
                    item.quantityProperty().removeListener(weakTotalsUpdater);
                    item.subtotalProperty().removeListener(weakTotalsUpdater);
                  }
                }
              }
              updateTotals();
            });
    discountAmountCents.addListener(weakTotalsUpdater);
    shippingCostCents.addListener(weakTotalsUpdater);
  }

  public void updateTotals() {
    long calculateSubtotalCents = items.stream().mapToLong(OrderItem::getSubtotal).sum();
    int calculateTotalItems = items.stream().mapToInt(OrderItem::getQuantity).sum();
    this.subtotalCents.set(calculateSubtotalCents);
    this.totalItems.set(calculateTotalItems);
    long calculateFinalAmountCents =
        calculateSubtotalCents
            - this.getDiscountAmountCents()
            + this.getShippingCostCents()
            + this.getTaxAmountCents();
    this.finalAmountCents.set(Math.max(0L, calculateFinalAmountCents));
  }

  public final int getCustomerId() {
    return this.customerId.get();
  }

  public final ReadOnlyIntegerProperty customerIdProperty() {
    return this.customerId.getReadOnlyProperty();
  }

  public final FulfillmentType getFulfillmentType() {
    return this.fulfillmentType.get();
  }

  public final void setFulfillmentType(FulfillmentType fulfillmentType) {
    this.fulfillmentType.set(fulfillmentType);
  }

  public final ObjectProperty<FulfillmentType> fulfillmentTypeProperty() {
    return this.fulfillmentType;
  }

  public final FulfillmentStatus getFulfillmentStatus() {
    return this.fulfillmentStatus.get();
  }

  public final void setFulfillmentStatus(FulfillmentStatus fulfillmentStatus) {
    this.fulfillmentStatus.set(fulfillmentStatus);
  }

  public final ObjectProperty<FulfillmentStatus> fulfillmentStatusProperty() {
    return this.fulfillmentStatus;
  }

  public final int getTotalItems() {
    return this.totalItems.get();
  }

  public final ReadOnlyIntegerProperty totalItemsProperty() {
    return this.totalItems.getReadOnlyProperty();
  }

  public final long getSubtotalCents() {
    return this.subtotalCents.get();
  }

  public final ReadOnlyLongProperty subtotalCentsProperty() {
    return this.subtotalCents.getReadOnlyProperty();
  }

  public final long getTaxAmountCents() {
    return this.taxAmountCents.get();
  }

  public final void setTaxAmountCents(long taxAmountCents) {
    this.taxAmountCents.set(taxAmountCents);
  }

  public final LongProperty taxAmountCentsProperty() {
    return taxAmountCents;
  }

  public final long getDiscountAmountCents() {
    return this.discountAmountCents.get();
  }

  public final void setDiscountAmountCents(long discountAmountCents) {
    this.discountAmountCents.set(discountAmountCents);
  }

  public final LongProperty discountAmountCentsProperty() {
    return discountAmountCents;
  }

  public final long getShippingCostCents() {
    return this.shippingCostCents.get();
  }

  public final void setShippingCostCents(long shippingCostCents) {
    this.shippingCostCents.set(shippingCostCents);
  }

  public final LongProperty shippingCostCentsProperty() {
    return this.shippingCostCents;
  }

  public final long getShippingCostActualCents() {
    return this.shippingCostActualCents.get();
  }

  public final void setShippingCostActualCents(long shippingCostActualCents) {
    this.shippingCostActualCents.set(shippingCostActualCents);
  }

  public final LongProperty shippingCostActualCentsProperty() {
    return this.shippingCostActualCents;
  }

  public final long getFinalAmountCents() {
    return this.finalAmountCents.get();
  }

  public final ReadOnlyLongProperty finalAmountCentsProperty() {
    return this.finalAmountCents.getReadOnlyProperty();
  }

  public final PaymentMethod getPaymentMethod() {
    return this.paymentMethod.get();
  }

  public final void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod.set(paymentMethod);
  }

  public final ObjectProperty<PaymentMethod> paymentMethodProperty() {
    return paymentMethod;
  }

  public final String getTrackingNumber() {
    return this.trackingNumber.get();
  }

  public final void setTrackingNumber(String trackingNumber) {
    this.trackingNumber.set(trackingNumber);
  }

  public final StringProperty trackingNumberProperty() {
    return this.trackingNumber;
  }

  public final Customer getCustomer() {
    return this.customer.get();
  }

  public final void setCustomer(Customer customer) {
    this.customer.set(customer);
    this.customerId.set(customer != null ? customer.getId() : -1);
  }

  public final ObjectProperty<Customer> customerProperty() {
    return this.customer;
  }

  public ObservableList<OrderItem> getItems() {
    return items;
  }

  public final void setItems(List<OrderItem> newItems) {
    this.items.setAll(newItems);
  }

  @Override
  public String toString() {
    String idString = (getId() == -1) ? "NEW_ORDER" : "#" + getId();
    String customerName = (getCustomer() != null) ? getCustomer().getFullName() : "Guest";
    String totalStr = String.format("$%.2f", getFinalAmountCents() / 100.0);
    String status = getFulfillmentStatus().toString();
    return String.format(
        "Order[%s | %s | Total: %s | Status: %s]", idString, customerName, totalStr, status);
  }
}
