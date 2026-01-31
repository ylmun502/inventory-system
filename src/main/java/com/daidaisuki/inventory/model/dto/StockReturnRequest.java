package com.daidaisuki.inventory.model.dto;

public record StockReturnRequest(
    int productId, int orderId, int batchId, int quantity, String reason) {}
