package com.daidaisuki.inventory.model.dto;

import java.math.BigDecimal;

public record StockAllocation(int batchId, int quantity, BigDecimal unitCost) {}
