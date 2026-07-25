package com.monika.monikamart.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static OrderStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        try {
            return OrderStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        if (this == CANCELLED || this == DELIVERED) {
            return false;
        }
        if (nextStatus == CANCELLED) {
            return this == PENDING || this == CONFIRMED;
        }
        switch (this) {
            case PENDING:
                return nextStatus == CONFIRMED;
            case CONFIRMED:
                return nextStatus == SHIPPED;
            case SHIPPED:
                return nextStatus == DELIVERED;
            default:
                return false;
        }
    }
}
