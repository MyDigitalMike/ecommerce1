package core.ecommerce.services;

import core.ecommerce.dto.OrderItemRequest;
import core.ecommerce.dto.OrderRequest;
import core.ecommerce.entity.*;
import core.ecommerce.repository.OrderRepository;
import core.ecommerce.repository.ProductRepository;
import core.ecommerce.repository.UserRepository;
import core.ecommerce.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurar fechas de descuento válidas
        ReflectionTestUtils.setField(orderService, "discountStart", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(orderService, "discountEnd", LocalDateTime.now().plusDays(1));
    }

    @Test
    void createOrder_shouldThrowIfUserNotFound() {
        when(userRepository.findByEmail("no@correo.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(new OrderRequest(), "no@correo.com"));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowIfProductNotFound() {
        User user = new User(1L, "Test", "123", "test@correo.com", new Role(1L, "CLIENT"));
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(99L, 1)), false);

        when(userRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request, "test@correo.com"));

        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void createOrder_shouldThrowIfNotEnoughStock() {
        User user = new User(1L, "Test", "123", "test@correo.com", new Role(1L, "CLIENT"));
        Product product = Product.builder().id(1L).name("Pan").price(100.0).stock(1).active(true).build();
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(1L, 2)), false);

        when(userRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request, "test@correo.com"));

        assertEquals("No hay suficiente stock para el producto: Pan", ex.getMessage());
    }
}
