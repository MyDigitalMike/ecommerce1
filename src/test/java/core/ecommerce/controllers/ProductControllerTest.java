package core.ecommerce.controllers;

import core.ecommerce.controller.ProductController;
import core.ecommerce.dto.ProductRequest;
import core.ecommerce.dto.ProductResponse;
import core.ecommerce.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        LocalDateTime now = LocalDateTime.now();
        List<ProductResponse> mockList = List.of(
                new ProductResponse(1L, "P1", "desc", 10.0, true, 5, now),
                new ProductResponse(2L, "P2", "desc", 15.0, true, 3, now));
        when(productService.getAll()).thenReturn(mockList);

        ResponseEntity<List<ProductResponse>> response = productController.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        List<ProductResponse> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void testCreateProduct() {
        LocalDateTime now = LocalDateTime.now();
        ProductRequest request = new ProductRequest("P3", "Pan", 20.0, 1, true);
        ProductResponse responseMock = new ProductResponse(3L, "P3", "Queso", 20.0, true, 1, now);
        when(productService.create(request)).thenReturn(responseMock);

        ResponseEntity<ProductResponse> response = productController.create(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        ProductResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("P3", responseBody.getName());
        verify(productService).create(request);
    }

    @Test
    void testUpdateStock() {
        LocalDateTime now = LocalDateTime.now();
        ProductResponse responseMock = new ProductResponse(1L, "P1", "Queso", 2.50, true, 15, now);
        when(productService.updateStock(1L, 15)).thenReturn(responseMock);

        ResponseEntity<ProductResponse> response = productController.updateStock(1L, 15);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        ProductResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(15, responseBody.getStock());
        verify(productService).updateStock(1L, 15);
    }
}