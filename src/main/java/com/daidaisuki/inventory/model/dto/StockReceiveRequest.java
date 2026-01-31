package com.daidaisuki.inventory.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StockReceiveRequest(
    int productId,
    int supplierId,
    String batchCode,
    int quantity,
    BigDecimal unitCost,
    OffsetDateTime expiryDate,
    String reason) {}
