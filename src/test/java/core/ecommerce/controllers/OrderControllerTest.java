package core.ecommerce.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import core.ecommerce.controller.OrderController;
import core.ecommerce.dto.OrderItemRequest;
import core.ecommerce.dto.OrderRequest;
import core.ecommerce.services.OrderService;
import core.ecommerce.util.JwtUtil;
import core.ecommerce.entity.Order;

public class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldCallServiceWithExtractedEmail() {
        String token = "mocked.jwt.token";
        String authHeader = "Bearer " + token;
        String email = "cliente@correo.com";

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(1L, 2)), false);

        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(orderService.createOrder(request, email)).thenReturn(new Order());

        ResponseEntity<String> response = orderController.createOrder(request, authHeader);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Orden registrada correctamente.", response.getBody());
        verify(jwtUtil).extractUsername(token);
        verify(orderService).createOrder(request, email);
    }
}
