package com.gae.supportorders.dto;

import com.gae.supportorders.domain.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        OrderStatus status
) {}
