package core.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.ecommerce.dto.ProductRequest;
import core.ecommerce.dto.ProductResponse;
import core.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }
        @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(productService.search(q));
    }
}
