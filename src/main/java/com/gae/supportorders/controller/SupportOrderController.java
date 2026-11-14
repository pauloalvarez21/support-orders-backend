package com.gae.supportorders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gae.supportorders.domain.OrderStatus;
import com.gae.supportorders.dto.CreateOrderRequest;
import com.gae.supportorders.dto.OrderResponse;
import com.gae.supportorders.dto.UpdateStatusRequest;
import com.gae.supportorders.service.SupportOrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/support-orders")
@RequiredArgsConstructor
@Slf4j
public class SupportOrderController {

    private final SupportOrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/support-orders - externalReference={}", request.externalReference());
        OrderResponse response = service.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse.OrderSummary>> listOrders(
            @RequestParam(required = false) Optional<OrderStatus> status) {
        log.debug("GET /api/support-orders - statusFilter={}", status.orElse(null));
        List<OrderResponse.OrderSummary> orders = service.listOrders(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.debug("GET /api/support-orders/{}", id);
        OrderResponse response = service.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("PATCH /api/support-orders/{}/status - newStatus={}", id, request.status());
        OrderResponse response = service.changeStatus(id, request.status());
        return ResponseEntity.ok(response);
    }
}