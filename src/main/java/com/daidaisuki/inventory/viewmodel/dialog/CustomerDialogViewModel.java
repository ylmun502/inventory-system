package com.daidaisuki.inventory.viewmodel.dialog;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.ui.validation.ValidationStatus;
import com.daidaisuki.inventory.util.StringCleaner;
import com.daidaisuki.inventory.util.ValidationUtils;
import com.daidaisuki.inventory.viewmodel.base.BaseDialogViewModel;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomerDialogViewModel extends BaseDialogViewModel<Customer> {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

  private final Customer customer;
  private final ObjectBinding<ValidationStatus> validationStatus;

  public final StringProperty fullName = new SimpleStringProperty("");
  public final StringProperty phoneNumber = new SimpleStringProperty("");
  public final StringProperty email = new SimpleStringProperty("");
  public final StringProperty address = new SimpleStringProperty("");
  public final StringProperty acquisitionSource = new SimpleStringProperty("");

  public CustomerDialogViewModel(Customer customerToEdit) {
    this.customer = customerToEdit;

    if (customerToEdit != null) {
      this.mapModelToProperties(customerToEdit);
    } else {
      this.resetProperties();
    }

    this.validationStatus =
        Bindings.createObjectBinding(
            this::validate, this.fullName, this.phoneNumber, this.email, this.acquisitionSource);
  }

  private ValidationStatus validate() {
    StringBuilder errors = new StringBuilder();
    String cleanFullName = StringCleaner.cleanOrNull(this.fullName.get());
    String cleanPhoneNumber = StringCleaner.cleanOrNull(this.phoneNumber.get());
    String cleanEmail = StringCleaner.cleanOrNull(this.email.get());
    String cleanAcquisitionSource = StringCleaner.cleanOrNull(this.acquisitionSource.get());

    ValidationUtils.isFieldEmpty(cleanFullName, "Full Name", errors);
    ValidationUtils.isFieldEmpty(cleanAcquisitionSource, "Acquisition Source", errors);
    if (cleanEmail != null && !EMAIL_PATTERN.matcher(cleanEmail).matches()) {
      errors.append("Email format is invalid.\n");
    }
    if (cleanPhoneNumber != null && !PHONENUMBER_PATTERN.matcher(cleanPhoneNumber).matches()) {
      errors.append("Phone number must be between 7-15 digits.\n");
    }
    return new ValidationStatus(errors.isEmpty(), errors.toString());
  }

  @Override
  public Customer createResult() {
    String cleanFullName = StringCleaner.cleanOrNull(this.fullName.get());
    String cleanPhoneNumber = StringCleaner.cleanOrNull(this.phoneNumber.get());
    String cleanEmail = StringCleaner.cleanOrNull(this.email.get());
    String cleanAddress = StringCleaner.cleanOrNull(this.address.get());
    String cleanAcquisitionSource = StringCleaner.cleanOrNull(this.acquisitionSource.get());

    if (this.customer == null) {
      Customer result = new Customer();
      result.setFullName(cleanFullName);
      result.setPhoneNumber(cleanPhoneNumber);
      result.setEmail(cleanEmail);
      result.setAddress(cleanAddress);
      result.setAcquisitionSource(cleanAcquisitionSource);
      return result;
    }
    return new Customer(
        this.customer.getId(),
        cleanFullName,
        cleanPhoneNumber,
        cleanEmail,
        cleanAddress,
        cleanAcquisitionSource,
        this.customer.getTotalOrders(),
        this.customer.getTotalSpent(),
        this.customer.getTotalDiscount(),
        this.customer.getAverageOrderValue(),
        this.customer.getLastOrderDate(),
        this.customer.getCreatedAt(),
        this.customer.getUpdatedAt(),
        this.customer.isDeleted());
  }

  @Override
  public BooleanBinding isInvalidProperty() {
    return Bindings.createBooleanBinding(
        () -> !this.validationStatus.get().isValid(), this.validationStatus);
  }

  @Override
  protected void resetProperties() {
    this.fullName.set("");
    this.phoneNumber.set("");
    this.email.set("");
    this.address.set("");
    this.acquisitionSource.set("");
  }

  @Override
  protected void mapModelToProperties(Customer model) {
    this.fullName.set(StringCleaner.cleanString(model.getFullName()));
    this.phoneNumber.set(StringCleaner.cleanString(model.getPhoneNumber()));
    this.email.set(StringCleaner.cleanString(model.getEmail()));
    this.address.set(StringCleaner.cleanString(model.getAddress()));
    this.acquisitionSource.set(StringCleaner.cleanString(model.getAcquisitionSource()));
  }

  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> this.customer == null);
  }
}
