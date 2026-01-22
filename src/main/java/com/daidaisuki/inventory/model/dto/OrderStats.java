package com.daidaisuki.inventory.model.dto;

import java.time.OffsetDateTime;

public record OrderStats(
    int totalOrders, long totalSpent, long totalDiscount, OffsetDateTime lastOrderDate) {}
