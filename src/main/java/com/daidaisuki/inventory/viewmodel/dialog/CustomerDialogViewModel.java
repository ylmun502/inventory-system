package com.daidaisuki.inventory.viewmodel.dialog;


/* Comment out during mvvm migration as need to refactor view by view

public class CustomerDialogViewModel extends BaseDialogViewModel<Customer> {
  private final CustomerService customerService;
  private final ObjectBinding<ValidationStatus> validationStatus;

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

  public final StringProperty fullName = new SimpleStringProperty("");
  public final StringProperty phoneNumber = new SimpleStringProperty("");
  public final StringProperty email = new SimpleStringProperty("");
  public final StringProperty address = new SimpleStringProperty("");
  public final StringProperty acquisitionSource = new SimpleStringProperty("");

  public CustomerDialogViewModel(CustomerService customerService) {
    this.customerService = customerService;
    this.validationStatus =
        Bindings.createObjectBinding(
            () -> {
              StringBuilder errors = new StringBuilder();
              ValidationUtils.isFieldEmpty(fullName.get(), "Full Name", errors);
              ValidationUtils.isFieldEmpty(acquisitionSource.get(), "Acquisition Source", errors);
              if (email.get().isBlank() && !EMAIL_PATTERN.matcher(email.get()).matches()) {
                errors.append("Email format is invalid.\n");
              }
              if (phoneNumber.get().isBlank()) {
                errors.append("Phone number format is invalid.\n");
              } else if (!PHONENUMBER_PATTERN.matcher(phoneNumber.get()).matches()) {
                errors.append("Phone number must be between 7-15 digits.\n");
              }
              return new ValidationStatus(errors.isEmpty(), errors.toString());
            },
            fullName,
            email,
            phoneNumber,
            acquisitionSource);
  }

  @Override
  protected ObjectBinding<ValidationStatus> validationStatusProperty() {
    return this.validationStatus;
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
  protected void mapModelToProperties() {
    if (this.model != null) {
      this.fullName.set(StringCleaner.cleanString(this.model.getFullName()));
      this.phoneNumber.set(StringCleaner.cleanString(this.model.getPhoneNumber()));
      this.email.set(StringCleaner.cleanString(this.model.getEmail()));
      this.address.set(StringCleaner.cleanString(this.model.getAddress()));
      this.acquisitionSource.set(StringCleaner.cleanString(this.model.getAcquisitionSource()));
    }
  }

  @Override
  protected Customer mapPropertiesToModel() {
    if (this.model == null) {
      this.model = new Customer();
    }
    this.model.setFullName(StringCleaner.cleanOrNull(this.fullName.get()));
    this.model.setPhoneNumber(StringCleaner.cleanOrNull(this.phoneNumber.get()));
    this.model.setEmail(StringCleaner.cleanOrNull(this.email.get()));
    this.model.setAddress(StringCleaner.cleanOrNull(this.address.get()));
    this.model.setAcquisitionSource(StringCleaner.cleanOrNull(this.acquisitionSource.get()));
    return this.model;
  }

  @Override
  public void save() throws Exception {
    Customer customer = mapPropertiesToModel();
    if (this.isNew()) {
      this.customerService.createCustomer(customer);
    } else {
      this.customerService.updateCustomer(customer);
    }
  }
}

*/
