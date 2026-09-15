package com.daidaisuki.inventory.serviceregistry;

<<<<<<< HEAD
import com.daidaisuki.inventory.service.InventoryService;
=======
import com.daidaisuki.inventory.service.CustomerService;
import com.daidaisuki.inventory.service.InventoryService;
import com.daidaisuki.inventory.service.OrderService;
>>>>>>> origin/new
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.service.SupplierService;
import java.sql.Connection;

public class ServiceRegistry {
  private final Connection connection;
  private ProductService productService;
  private InventoryService inventoryService;
  private SupplierService supplierService;
<<<<<<< HEAD
=======
  private CustomerService customerService;
  private OrderService orderService;
>>>>>>> origin/new

  public ServiceRegistry(Connection connection) {
    this.connection = connection;
  }

  public ProductService getProductService() {
<<<<<<< HEAD
    if (productService == null) {
      this.productService = new ProductService(connection);
=======
    if (this.productService == null) {
      this.productService = new ProductService(this.connection);
>>>>>>> origin/new
    }
    return this.productService;
  }

  public InventoryService getInventoryService() {
<<<<<<< HEAD
    if (inventoryService == null) {
      this.inventoryService = new InventoryService(connection);
=======
    if (this.inventoryService == null) {
      this.inventoryService = new InventoryService(this.connection);
>>>>>>> origin/new
    }
    return this.inventoryService;
  }

<<<<<<< HEAD
  public SupplierService getSupplierService() {
    if(supplierService == null) {
        this.supplierService = new SupplierService(connection);
=======
  public CustomerService getCustomerService() {
    if (this.customerService == null) {
      this.customerService = new CustomerService(this.connection);
    }
    return this.customerService;
  }

  public OrderService getOrderService() {
    if (orderService == null) {
      this.orderService = new OrderService(this.connection);
    }
    return this.orderService;
  }

  public SupplierService getSupplierService() {
    if (supplierService == null) {
      this.supplierService = new SupplierService(connection);
>>>>>>> origin/new
    }
    return this.supplierService;
  }
}
