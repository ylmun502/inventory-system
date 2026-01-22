package com.daidaisuki.inventory.model.dto;

import com.daidaisuki.inventory.enums.TransactionType;

public record StockAdjustRequest(
    int productId, int changeAmount, TransactionType type, String reason) {}
