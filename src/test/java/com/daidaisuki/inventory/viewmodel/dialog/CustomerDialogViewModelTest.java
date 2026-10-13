package com.daidaisuki.inventory.viewmodel.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.daidaisuki.inventory.model.Customer;
import com.daidaisuki.inventory.model.builder.CustomerBuilder;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CustomerDialogViewModelTest {
  @Test
  void newCustomerShouldStartInvalid() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    assertTrue(viewModel.isNewProperty().get());
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void requiredFieldsShouldMakeCustomerValid() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankFullNameShouldBeInvalid() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("   ");
    viewModel.acquisitionSource.set("Instagram");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankAcquisitionSourceShouldBeInvalid() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("   ");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void validEmailShouldBeAccepted() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    viewModel.email.set("jane@example.com");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void invalidEmailShouldBeRejected() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    viewModel.email.set("not an email");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankEmailShouldBeAllowed() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    viewModel.email.set("   ");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void validPhoneNumberShouldBeAccepted() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    viewModel.phoneNumber.set("+18166661239");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void phoneNumberWithSevenDigitsShouldBeAccepted() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instgram");
    viewModel.phoneNumber.set("1234567");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void phoneNumberWithFifteenDigitsShouldBeAccepted() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instgram");
    viewModel.phoneNumber.set("123456789012345");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void phoneNumberShorterThanSevenDigitsShouldBeRejected() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instgram");
    viewModel.phoneNumber.set("123456");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void phoneNumberLongerThanFifteenDigitsShouldBeRejected() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instgram");
    viewModel.phoneNumber.set("1234567890123456");
    assertTrue(viewModel.isInvalidProperty().get());
  }

  @Test
  void blankPhoneNumberShouldBeAllowed() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    viewModel.phoneNumber.set("   ");
    assertFalse(viewModel.isInvalidProperty().get());
  }

  @Test
  void creeateResultShouldNormalizeInput() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("  Jane Doe  ");
    viewModel.phoneNumber.set("  1234567890  ");
    viewModel.email.set("  jane@example.com  ");
    viewModel.address.set("  123 Main St  ");
    viewModel.acquisitionSource.set("  Instagram  ");
    Customer result = viewModel.createResult();
    assertEquals("Jane Doe", result.getFullName());
    assertEquals("1234567890", result.getPhoneNumber());
    assertEquals("jane@example.com", result.getEmail());
    assertEquals("123 Main St", result.getAddress());
    assertEquals("Instagram", result.getAcquisitionSource());
  }

  @Test
  void blankOptionalFieldsShouldBecomeNull() {
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(null);
    viewModel.fullName.set("Jane Doe");
    viewModel.acquisitionSource.set("Instagram");
    viewModel.phoneNumber.set("   ");
    viewModel.email.set("");
    viewModel.address.set("   ");
    Customer result = viewModel.createResult();
    assertNull(result.getPhoneNumber());
    assertNull(result.getEmail());
    assertNull(result.getAddress());
  }

  @Test
  void editShouldPopulatePropertiesFromCustomer() {
    Customer customer =
        new CustomerBuilder()
            .withFullName("Existing Customer")
            .withPhoneNumber("9876543210")
            .withEmail("existing@example.com")
            .withAddress("456 Market St")
            .withAcquisitionSource("Instagram")
            .build();
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(customer);
    assertFalse(viewModel.isNewProperty().get());
    assertFalse(viewModel.isInvalidProperty().get());
    assertEquals("Existing Customer", viewModel.fullName.get());
    assertEquals("9876543210", viewModel.phoneNumber.get());
    assertEquals("existing@example.com", viewModel.email.get());
    assertEquals("456 Market St", viewModel.address.get());
    assertEquals("Instagram", viewModel.acquisitionSource.get());
  }

  @Test
  void editShouldReturnCopyWithoutMutatingOriginal() {
    Customer original =
        new CustomerBuilder()
            .withId(10)
            .withFullName("Original Customer")
            .withPhoneNumber("1111111111")
            .withEmail("old@example.com")
            .withAddress("Old Address")
            .withAcquisitionSource("Instagram")
            .build();
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(original);
    viewModel.fullName.set("Updated Customer");
    viewModel.phoneNumber.set("2222222222");
    viewModel.email.set("new@example.com");
    viewModel.address.set("New Address");
    viewModel.acquisitionSource.set("Facebook");
    Customer result = viewModel.createResult();
    assertNotSame(original, result);
    assertEquals("Original Customer", original.getFullName());
    assertEquals("1111111111", original.getPhoneNumber());
    assertEquals("old@example.com", original.getEmail());
    assertEquals("Old Address", original.getAddress());
    assertEquals("Instagram", original.getAcquisitionSource());
    assertEquals("Updated Customer", result.getFullName());
    assertEquals("2222222222", result.getPhoneNumber());
    assertEquals("new@example.com", result.getEmail());
    assertEquals("New Address", result.getAddress());
    assertEquals("Facebook", result.getAcquisitionSource());
  }

  @Test
  void editShouldPreserveSummaryAndPersistentFields() {
    OffsetDateTime lastOrderDate = OffsetDateTime.parse("2025-01-18T09:00:00Z");
    OffsetDateTime createdAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
    OffsetDateTime updatedAt = OffsetDateTime.parse("2025-02-02T08:00:00Z");
    Customer original =
        new CustomerBuilder()
            .withId(25)
            .withTotalOrders(15)
            .withTotalSpent(new BigDecimal("270.90"))
            .withTotalDiscount(new BigDecimal("20.88"))
            .withAverageOrderValue(new BigDecimal("18.06"))
            .withLastOrderDate(lastOrderDate)
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .deleted()
            .build();
    CustomerDialogViewModel viewModel = new CustomerDialogViewModel(original);
    viewModel.fullName.set("Updated Customer");
    Customer result = viewModel.createResult();
    assertEquals(25, result.getId());
    assertEquals(15, result.getTotalOrders());
    assertEquals(new BigDecimal("270.90"), result.getTotalSpent());
    assertEquals(new BigDecimal("20.88"), result.getTotalDiscount());
    assertEquals(new BigDecimal("18.06"), result.getAverageOrderValue());
    assertEquals(lastOrderDate, result.getLastOrderDate());
    assertEquals(createdAt, result.getCreatedAt());
    assertEquals(updatedAt, result.getUpdatedAt());
    assertTrue(result.isDeleted());
  }
}
