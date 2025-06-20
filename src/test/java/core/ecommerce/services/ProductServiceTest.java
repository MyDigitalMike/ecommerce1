package core.ecommerce.services;

import core.ecommerce.config.DiscountProperties;
import core.ecommerce.dto.ProductRequest;
import core.ecommerce.dto.ProductResponse;
import core.ecommerce.entity.Product;
import core.ecommerce.repository.ProductRepository;

import core.ecommerce.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private DiscountProperties discountProperties;
    private AuditService auditService;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        discountProperties = mock(DiscountProperties.class);
        auditService = mock(AuditService.class);

        productService = new ProductServiceImpl(
                discountProperties,
                productRepository,
                auditService);
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Description");
        request.setPrice(100.0);
        request.setStock(10);
        LocalDateTime now = LocalDateTime.now();
        Product savedProduct = Product.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .active(true)
                .stock(request.getStock())
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(discountProperties.getTimeStart()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(discountProperties.getTimeEnd()).thenReturn(LocalDateTime.of(2024, 12, 31, 23, 59));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse response = productService.create(request);

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        verify(productRepository).save(any(Product.class));
        verify(auditService).log(
                contains("Producto creado"),
                eq("1"),
                eq("CREATE"),
                eq("SYSTEM"),
                anyString());
    }

    @Test
    void testGetAllProducts() {
        LocalDateTime now = LocalDateTime.now();

        Product product1 = Product.builder().id(1L).name("P1").price(2.50).active(true).stock(10)
                .createdAt(now)
                .updatedAt(now).build();
        Product product2 = Product.builder().id(2L).name("P2").price(2.50).active(true).stock(10)
                .createdAt(now)
                .updatedAt(now).build();
        when(discountProperties.getTimeStart()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(discountProperties.getTimeEnd()).thenReturn(LocalDateTime.of(2024, 12, 31, 23, 59));
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductResponse> products = productService.getAll();

        assertEquals(2, products.size());
        assertEquals("P1", products.get(0).getName());
        assertEquals("P2", products.get(1).getName());
        verify(productRepository).findAll();
    }

    @Test
    void testGetActiveProducts() {
        LocalDateTime now = LocalDateTime.now();
        Product product1 = Product.builder()
                .id(1L)
                .name("P1")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        Product product2 = Product.builder()
                .id(2L)
                .name("P2")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(discountProperties.getTimeStart()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(discountProperties.getTimeEnd()).thenReturn(LocalDateTime.of(2024, 12, 31, 23, 59));
        when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product1, product2));

        List<ProductResponse> products = productService.getActiveProducts();

        assertEquals(2, products.size());
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void testUpdateStock_ActiveToInactive() {
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .id(1L)
                .name("P1")
                .price(2.50)
                .stock(5)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(discountProperties.getTimeStart()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(discountProperties.getTimeEnd()).thenReturn(LocalDateTime.of(2024, 12, 31, 23, 59));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.updateStock(1L, 0);

        assertNotNull(response);
        assertEquals(0, response.getStock());
        assertFalse(product.isActive());
        verify(productRepository).save(product);
        verify(auditService).log(
                contains("Stock actualizado"),
                eq("1"),
                eq("UPDATE"),
                eq("SYSTEM"),
                contains("Nuevo stock: 0"));
    }

    @Test
    void testUpdateStock_ActiveRemains() {
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .id(2L)
                .name("P2")
                .price(2.50)
                .stock(5)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(discountProperties.getTimeStart()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(discountProperties.getTimeEnd()).thenReturn(LocalDateTime.of(2024, 12, 31, 23, 59));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.updateStock(2L, 10);

        assertNotNull(response);
        assertEquals(10, response.getStock());
        assertTrue(product.isActive());
        verify(productRepository).save(product);
        verify(auditService).log(
                contains("Stock actualizado"),
                eq("2"),
                eq("UPDATE"),
                eq("SYSTEM"),
                contains("Nuevo stock: 10"));
    }

    @Test
    void testUpdateStock_ProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.updateStock(99L, 5);
        });

        assertEquals("Producto no encontrado", ex.getMessage());
    }
}