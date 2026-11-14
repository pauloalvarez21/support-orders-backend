package com.gae.supportorders.dto;

import java.time.LocalDateTime;

import com.gae.supportorders.domain.Category;
import com.gae.supportorders.domain.Channel;
import com.gae.supportorders.domain.OrderStatus;
import com.gae.supportorders.domain.Priority;
import com.gae.supportorders.domain.SupportOrder;

public record OrderResponse(
        Long id,
        String externalReference,
        String customerId,
        Category category,
        String description,
        Channel channel,
        Priority priority,
        OrderStatus status,
        LocalDateTime createdAt
) {
    
    public static OrderResponse fromEntity(SupportOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getExternalReference(),
                order.getCustomerId(),
                order.getCategory(),
                order.getDescription(),
                order.getChannel(),
                order.getPriority(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
    
    public record OrderSummary(
            Long id,
            String externalReference,
            Category category,
            Priority priority,
            OrderStatus status,
            LocalDateTime createdAt
    ) {
        public static OrderSummary fromEntity(SupportOrder order) {
            return new OrderSummary(
                    order.getId(),
                    order.getExternalReference(),
                    order.getCategory(),
                    order.getPriority(),
                    order.getStatus(),
                    order.getCreatedAt()
            );
        }
    }
}
