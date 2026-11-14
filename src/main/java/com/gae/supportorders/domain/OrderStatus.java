package com.gae.supportorders.domain;

public enum OrderStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case OPEN -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS -> newStatus == RESOLVED || newStatus == CANCELLED;
            case RESOLVED, CANCELLED -> false;
        };
    }

    public boolean isActive() {
        return this == OPEN || this == IN_PROGRESS;
    }
}
