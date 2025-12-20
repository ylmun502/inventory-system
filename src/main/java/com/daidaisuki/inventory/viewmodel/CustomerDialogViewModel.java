package com.daidaisuki.inventory.viewmodel;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.util.ValidationUtils;
import java.util.regex.Pattern;

public class CustomerDialogViewModel {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

  private Customer customer;

  public CustomerDialogViewModel(Customer customer) {
    this.customer = customer != null ? customer : new Customer();
  }

  public Customer getCustomer() {
    return this.customer;
  }

  public String validateAndUpdateCustomer(
      String name, String phoneNumber, String email, String address, String platform) {
    StringBuilder errorMessage = new StringBuilder();
    ValidationUtils.isFieldEmpty(name, "Name", errorMessage);
    ValidationUtils.isFieldEmpty(platform, "Platform", errorMessage);
    if (email != null && !email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
      errorMessage.append("Email format is invalid.\n");
    }
    if (phoneNumber != null
        && !phoneNumber.isBlank()
        && !PHONENUMBER_PATTERN.matcher(phoneNumber).matches()) {
      errorMessage.append("Phone number format is invalid.\n");
    }

    if (errorMessage.length() > 0) {
      return errorMessage.toString();
    }

    customer.setName(name);
    customer.setPhoneNumber(phoneNumber);
    customer.setEmail(email);
    customer.setAddress(address);
    customer.setPlatform(platform);

    return "";
  }
}
