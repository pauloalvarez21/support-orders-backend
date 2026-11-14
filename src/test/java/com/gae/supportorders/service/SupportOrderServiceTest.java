package com.gae.supportorders.service;

import com.gae.supportorders.domain.*;
import com.gae.supportorders.dto.CreateOrderRequest;
import com.gae.supportorders.dto.OrderResponse;
import com.gae.supportorders.exception.DuplicateActiveOrderException;
import com.gae.supportorders.exception.ResourceNotFoundException;
import com.gae.supportorders.repository.SupportOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportOrderServiceTest {

    @Mock
    private SupportOrderRepository repository;

    @InjectMocks
    private SupportOrderService service;

    private CreateOrderRequest validRequest;
    private SupportOrder sampleOrder;

    @BeforeEach
    void setUp() {
        validRequest = new CreateOrderRequest(
                "MOB-1001", "CUST-001", Category.PAYMENT,
                "El pago aparece como pendiente después de varios minutos.",
                Channel.MOBILE_APP, Priority.MEDIUM
        );

        sampleOrder = SupportOrder.builder()
                .id(1L)
                .externalReference("MOB-1001")
                .customerId("CUST-001")
                .category(Category.PAYMENT)
                .description("El pago aparece como pendiente después de varios minutos.")
                .channel(Channel.MOBILE_APP)
                .priority(Priority.MEDIUM)
                .status(OrderStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("1. Creación exitosa de una orden válida")
    void createOrder_Success() {
        when(repository.findActiveByExternalReference("MOB-1001")).thenReturn(Optional.empty());
        when(repository.save(any(SupportOrder.class))).thenReturn(sampleOrder);

        OrderResponse response = service.createOrder(validRequest);

        assertNotNull(response);
        assertEquals("MOB-1001", response.externalReference());
        assertEquals(OrderStatus.OPEN, response.status());
        verify(repository, times(1)).save(any(SupportOrder.class));
    }

    @Test
    @DisplayName("2. Rechazo de solicitud inválida (Referencia externa duplicada activa)")
    void createOrder_DuplicateActiveReference_ThrowsException() {
        when(repository.findActiveByExternalReference("MOB-1001")).thenReturn(Optional.of(sampleOrder));

        DuplicateActiveOrderException exception = assertThrows(
                DuplicateActiveOrderException.class,
                () -> service.createOrder(validRequest)
        );

        assertTrue(exception.getMessage().contains("Ya existe una orden activa"));
        verify(repository, never()).save(any(SupportOrder.class));
    }

    @Test
    @DisplayName("3. Rechazo de transición de estado no permitida")
    void changeStatus_InvalidTransition_ThrowsException() {
        sampleOrder.setStatus(OrderStatus.RESOLVED); // Estado final
        when(repository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.changeStatus(1L, OrderStatus.OPEN)
        );

        assertTrue(exception.getMessage().contains("Transición de estado no permitida"));
    }

    @Test
    @DisplayName("4. Consulta de orden inexistente")
    void getOrderById_NotFound_ThrowsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getOrderById(99L)
        );

        assertEquals("Orden no encontrada con id: 99", exception.getMessage());
    }
}