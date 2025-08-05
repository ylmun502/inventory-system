package com.daidaisuki.inventory.model.dto;

public class OrderStats {
    private final int totalOrders;
    private final double totalSpent;
    private final double totalDiscount;

    public OrderStats(int totalOrders, double totalSpent, double totalDiscount) {
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.totalDiscount = totalDiscount;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }
}
