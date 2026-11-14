package com.gae.supportorders.dto;

import com.gae.supportorders.domain.Category;
import com.gae.supportorders.domain.Channel;
import com.gae.supportorders.domain.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "externalReference es obligatorio")
        String externalReference,

        @NotBlank(message = "customerId es obligatorio")
        String customerId,

        @NotNull(message = "category es obligatoria")
        Category category,

        @NotBlank(message = "description es obligatoria")
        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        String description,

        @NotNull(message = "channel es obligatorio")
        Channel channel,

        @NotNull(message = "priority es obligatoria")
        Priority priority
) {}
