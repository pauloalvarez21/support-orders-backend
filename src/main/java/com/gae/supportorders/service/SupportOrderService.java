package com.gae.supportorders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gae.supportorders.domain.OrderStatus;
import com.gae.supportorders.domain.SupportOrder;
import com.gae.supportorders.dto.CreateOrderRequest;
import com.gae.supportorders.dto.OrderResponse;
import com.gae.supportorders.exception.DuplicateActiveOrderException;
import com.gae.supportorders.exception.ResourceNotFoundException;
import com.gae.supportorders.repository.SupportOrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportOrderService {

    private final SupportOrderRepository repository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Optional<SupportOrder> existingActiveOrder = repository.findActiveByExternalReference(request.externalReference());
        
        if (existingActiveOrder.isPresent()) {
            log.warn("Intento de crear orden duplicada con externalReference: {}", request.externalReference());
            throw new DuplicateActiveOrderException(
                String.format("Ya existe una orden activa con externalReference: %s", request.externalReference())
            );
        }

        SupportOrder newOrder = SupportOrder.builder()
                .externalReference(request.externalReference())
                .customerId(request.customerId())
                .category(request.category())
                .description(request.description())
                .channel(request.channel())
                .priority(request.priority())
                .status(OrderStatus.OPEN)
                .build();

        SupportOrder savedOrder = repository.save(newOrder);
        
        log.info("Orden creada exitosamente: id={}, externalReference={}", savedOrder.getId(), savedOrder.getExternalReference());
        
        return OrderResponse.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse.OrderSummary> listOrders(Optional<OrderStatus> statusFilter) {
        List<SupportOrder> orders = repository.findByStatus(statusFilter);
        
        return orders.stream()
                .map(OrderResponse.OrderSummary::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        SupportOrder order = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Orden no encontrada con id: {}", id);
                    return new ResourceNotFoundException("Orden no encontrada con id: " + id);
                });
        
        return OrderResponse.fromEntity(order);
    }

    @Transactional
    public OrderResponse changeStatus(Long id, OrderStatus newStatus) {
        SupportOrder order = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de cambiar estado de orden inexistente: id={}", id);
                    return new ResourceNotFoundException("Orden no encontrada con id: " + id);
                });

        order.changeStatus(newStatus);
        
        SupportOrder updatedOrder = repository.save(order);
        
        log.info("Estado de orden actualizado: id={}, de {} a {}", id, order.getStatus(), newStatus);
        
        return OrderResponse.fromEntity(updatedOrder);
    }
}
