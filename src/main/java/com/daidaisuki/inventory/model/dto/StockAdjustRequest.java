package com.daidaisuki.inventory.model.dto;

import com.daidaisuki.inventory.enums.TransactionType;
<<<<<<< HEAD

public record StockAdjustRequest(
    int productId, int changeAmount, TransactionType type, String reason) {}
=======
import java.math.BigDecimal;

public record StockAdjustRequest(
    int productId, int changeAmount, BigDecimal unitCost, TransactionType type, String reason) {}
>>>>>>> origin/new
