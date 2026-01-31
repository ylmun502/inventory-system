package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReceiveStockDialogViewModel extends BaseDialogViewModel<StockReceiveRequest> {
  private final Product product;
  private final ObservableList<Supplier> suppliers = FXCollections.observableArrayList();

  private final ObjectProperty<Supplier> selectedSupplier = new SimpleObjectProperty<>();
  private final StringProperty batchCode = new SimpleStringProperty();
  private final StringProperty quantity = new SimpleStringProperty();
  private final StringProperty unitCost = new SimpleStringProperty();
  private final ObjectProperty<LocalDate> expiryDate = new SimpleObjectProperty<>();
  private final StringProperty reason = new SimpleStringProperty();

  public ReceiveStockDialogViewModel(Product product, List<Supplier> supplierList) {
    this.product = product;
    this.suppliers.setAll(supplierList);
  }

  public void generateBatchCode() {
    Supplier supplier = selectedSupplier.get();
    String prefix = supplier != null ? supplier.getShortCode() : "SYS";
    String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String randomPart = String.format("%04d", (int) (Math.random() * 10000));
    this.batchCode.set(prefix + "-" + datePart + "-" + randomPart);
  }

  @Override
  public StockReceiveRequest createResult() {
    return new StockReceiveRequest(
        product.getId(),
        selectedSupplier.get().getId(),
        batchCode.get(),
        Integer.parseInt(quantity.get()),
        new BigDecimal(unitCost.get()),
        expiryDate.get() != null
            ? expiryDate.get().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
            : null,
        reason.get());
  }

  @Override
  public BooleanBinding isInvalidProperty() {
    return selectedSupplier.isNull().or(batchCode.isEmpty()).or(quantity.isEmpty());
  }

  @Override
  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> true);
  }

  @Override
  public void resetProperties() {
    batchCode.set("");
    quantity.set("");
    unitCost.set("");
    reason.set("");
  }

  public final Product getProduct() {
    return this.product;
  }

  public ObservableList<Supplier> getSuppliers() {
    return this.suppliers;
  }

  public Supplier getSelectedSupplier() {
    return this.selectedSupplier.get();
  }

  public ObjectProperty<Supplier> selectedSupplierProperty() {
    return this.selectedSupplier;
  }

  public final String getBatchCode() {
    return this.batchCode.get();
  }

  public final StringProperty batchCodeProperty() {
    return this.batchCode;
  }

  public final String getQuantity() {
    return this.quantity.get();
  }

  public final StringProperty quantityProperty() {
    return this.quantity;
  }

  public final String getUnitCost() {
    return this.unitCost.get();
  }

  public final StringProperty unitCostProperty() {
    return this.unitCost;
  }

  public LocalDate getExpiryDate() {
    return this.expiryDate.get();
  }

  public ObjectProperty<LocalDate> expiryDateProperty() {
    return this.expiryDate;
  }

  public final String getReason() {
    return this.reason.get();
  }

  public final StringProperty reasonProperty() {
    return this.reason;
  }
}
