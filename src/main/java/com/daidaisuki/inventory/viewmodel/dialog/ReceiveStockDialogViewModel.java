package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Product;
import com.daidaisuki.inventory.model.Supplier;
import com.daidaisuki.inventory.model.dto.StockReceiveRequest;
import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import com.daidaisuki.inventory.util.ValidationUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReceiveStockDialogViewModel extends BaseDialogViewModel<StockReceiveRequest> {
  private final Product product;
  private final ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
  private final ObjectBinding<ValidationStatus> validationStatus;

  private final ObjectProperty<Supplier> selectedSupplier = new SimpleObjectProperty<>();
  private final StringProperty batchCode = new SimpleStringProperty("");
  private final StringProperty quantity = new SimpleStringProperty("");
  private final StringProperty unitCost = new SimpleStringProperty("");
  private final ObjectProperty<LocalDate> expiryDate = new SimpleObjectProperty<>();
  private final StringProperty reason = new SimpleStringProperty("");

  public ReceiveStockDialogViewModel(Product selectedProduct, List<Supplier> supplierList) {
    this.product = Objects.requireNonNull(selectedProduct, "Product is required");
    this.resetProperties();
    if (supplierList == null) {
      this.suppliers.add(
          new Supplier(0, "Please add a supplier first", "None", "", "", "", null, null, false));
    } else {
      this.suppliers.setAll(supplierList);
    }
    if (!suppliers.isEmpty()) {
      selectedSupplier.set(suppliers.get(0));
    }

    this.validationStatus =
        Bindings.createObjectBinding(
            () -> {
              StringBuilder errors = new StringBuilder();
              ValidationUtils.isNumeric(this.quantity.get(), "Quantity", errors, false);
              ValidationUtils.isNumeric(this.unitCost.get(), "Unit Cost", errors, true);
              return new ValidationStatus(errors.isEmpty(), errors.toString());
            },
            this.quantity,
            this.unitCost);
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
        this.product.getId(),
        this.selectedSupplier.get().getId(),
        this.batchCode.get(),
        Integer.parseInt(this.quantity.get()),
        new BigDecimal(this.unitCost.get()),
        this.expiryDate.get() != null
            ? this.expiryDate.get().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
            : null,
        this.reason.get());
  }

  @Override
  public BooleanBinding isInvalidProperty() {
    return supplierOrBatchInvalidBinding()
        .or(
            Bindings.createBooleanBinding(
                () -> !this.validationStatus.get().isValid(), this.validationStatus));
  }

  private BooleanBinding supplierOrBatchInvalidBinding() {
    return selectedSupplier
        .isNull()
        .or(
            Bindings.createBooleanBinding(
                () -> selectedSupplier.get() != null && selectedSupplier.get().getId() == 0,
                selectedSupplier))
        .or(batchCode.isEmpty());
  }

  @Override
  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> true);
  }

  @Override
  public void mapModelToProperties(StockReceiveRequest model) {}

  @Override
  public void resetProperties() {
    this.batchCode.set("");
    this.quantity.set("");
    this.unitCost.set("");
    this.reason.set("");
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
