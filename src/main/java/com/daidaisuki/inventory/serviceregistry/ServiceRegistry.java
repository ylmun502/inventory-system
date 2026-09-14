package com.daidaisuki.inventory.serviceregistry;

import com.daidaisuki.inventory.service.CustomerService;
import com.daidaisuki.inventory.service.InventoryService;
import com.daidaisuki.inventory.service.OrderService;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.service.SupplierService;
import java.sql.Connection;

public class ServiceRegistry {
  private final Connection connection;
  private ProductService productService;
  private InventoryService inventoryService;
  private SupplierService supplierService;
  private CustomerService customerService;
  private OrderService orderService;

  public ServiceRegistry(Connection connection) {
    this.connection = connection;
  }

  public ProductService getProductService() {
    if (this.productService == null) {
      this.productService = new ProductService(this.connection);
    }
    return this.productService;
  }

  public InventoryService getInventoryService() {
    if (this.inventoryService == null) {
      this.inventoryService = new InventoryService(this.connection);
    }
    return this.inventoryService;
  }

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
    }
    return this.supplierService;
  }
}
