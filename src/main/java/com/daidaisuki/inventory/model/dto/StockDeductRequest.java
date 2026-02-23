package com.daidaisuki.inventory.model.dto;

import com.daidaisuki.inventory.enums.TransactionType;

public record StockDeductRequest(
    int productId, int quantity, TransactionType type, String reason) {}
