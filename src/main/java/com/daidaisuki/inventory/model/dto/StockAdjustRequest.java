package com.daidaisuki.inventory.model.dto;

import com.daidaisuki.inventory.enums.TransactionType;
import java.math.BigDecimal;

public record StockAdjustRequest(
    int productId, int changeAmount, BigDecimal unitCost, TransactionType type, String reason) {}
