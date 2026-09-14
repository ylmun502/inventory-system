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
  private final Customer customer;
  private final ObjectBinding<ValidationStatus> validationStatus;

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

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
            () -> {
              StringBuilder errors = new StringBuilder();
              String cleanFullName = StringCleaner.cleanOrNull(this.fullName.get());
              String cleanPhoneNumber = StringCleaner.cleanOrNull(this.phoneNumber.get());
              String cleanEmail = StringCleaner.cleanOrNull(this.email.get());
              String cleanAcquisitionSource =
                  StringCleaner.cleanOrNull(this.acquisitionSource.get());

              ValidationUtils.isFieldEmpty(cleanFullName, "Full Name", errors);
              ValidationUtils.isFieldEmpty(cleanAcquisitionSource, "Acquisition Source", errors);

              if (cleanEmail != null && !EMAIL_PATTERN.matcher(cleanEmail).matches()) {
                errors.append("Email format is invalid.\n");
              }
              if (cleanPhoneNumber != null
                  && !PHONENUMBER_PATTERN.matcher(cleanPhoneNumber).matches()) {
                errors.append("Phone number must be between 7-15 digits.\n");
              }
              return new ValidationStatus(errors.isEmpty(), errors.toString());
            },
            this.fullName,
            this.phoneNumber,
            this.email,
            this.address,
            this.acquisitionSource);
  }

  @Override
  public Customer createResult() {
    Customer result = this.customer == null ? new Customer() : this.customer;
    result.setFullName(this.fullName.get());
    result.setPhoneNumber(this.phoneNumber.get());
    result.setEmail(this.email.get());
    result.setAddress(this.address.get());
    result.setAcquisitionSource(this.acquisitionSource.get());
    return result;
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
    this.fullName.set(model.getFullName());
    this.phoneNumber.set(model.getPhoneNumber());
    this.email.set(model.getEmail());
    this.address.set(model.getAddress());
    this.acquisitionSource.set(model.getAcquisitionSource());
  }

  public BooleanBinding isNewProperty() {
    return Bindings.createBooleanBinding(() -> this.customer == null);
  }

  public Customer getCustomer() {
    return this.customer;
  }
}
