package com.daidaisuki.inventory.serviceregistry;

import com.daidaisuki.inventory.service.InventoryService;
import com.daidaisuki.inventory.service.ProductService;
import com.daidaisuki.inventory.service.SupplierService;
import java.sql.Connection;

public class ServiceRegistry {
  private final Connection connection;
  private ProductService productService;
  private InventoryService inventoryService;
  private SupplierService supplierService;

  public ServiceRegistry(Connection connection) {
    this.connection = connection;
  }

  public ProductService getProductService() {
    if (productService == null) {
      this.productService = new ProductService(connection);
    }
    return this.productService;
  }

  public InventoryService getInventoryService() {
    if (inventoryService == null) {
      this.inventoryService = new InventoryService(connection);
    }
    return this.inventoryService;
  }

  public SupplierService getSupplierService() {
    if(supplierService == null) {
        this.supplierService = new SupplierService(connection);
    }
    return this.supplierService;
  }
}
