package com.gae.supportorders.controller;

import tools.jackson.databind.ObjectMapper;
import com.gae.supportorders.domain.*;
import com.gae.supportorders.dto.CreateOrderRequest;
import com.gae.supportorders.service.SupportOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupportOrderController.class)
class SupportOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SupportOrderService service;

    @Test
    @DisplayName("5. Prueba de integración: Crear orden devuelve 201 Created y JSON válido")
    void createOrder_Returns201Created() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "MOB-1002", "CUST-002", Category.TECHNICAL,
                "La aplicación se cierra al iniciar sesión.",
                Channel.MOBILE_APP, Priority.HIGH
        );

        // Simulamos la respuesta del servicio
        when(service.createOrder(any(CreateOrderRequest.class))).thenReturn(
                new com.gae.supportorders.dto.OrderResponse(
                        1L, "MOB-1002", "CUST-002", Category.TECHNICAL,
                        "La aplicación se cierra al iniciar sesión.", Channel.MOBILE_APP,
                        Priority.HIGH, OrderStatus.OPEN, java.time.LocalDateTime.now()
                )
        );

        mockMvc.perform(post("/api/support-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.externalReference").value("MOB-1002"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("5b. Prueba de integración: Validación fallida devuelve 400 Bad Request")
    void createOrder_InvalidDescription_Returns400BadRequest() throws Exception {
        CreateOrderRequest invalidRequest = new CreateOrderRequest(
                "MOB-1003", "CUST-003", Category.PAYMENT,
                "Corto", // Menos de 10 caracteres
                Channel.MOBILE_APP, Priority.LOW
        );

        mockMvc.perform(post("/api/support-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.fieldErrors.description").value("La descripción debe tener entre 10 y 500 caracteres"));
    }
}