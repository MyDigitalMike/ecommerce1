package core.ecommerce.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import core.ecommerce.dto.ProductRequest;
import core.ecommerce.dto.ProductResponse;
import core.ecommerce.entity.Product;
import core.ecommerce.repository.ProductRepository;
import core.ecommerce.services.AuditService;
import core.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    @Value("${discount.random.enabled:false}")
    private boolean randomDiscountEnabled;

    @Value("${discount.time.start}")
    private LocalDateTime discountStart;

    @Value("${discount.time.end}")
    private LocalDateTime discountEnd;

    private final AuditService auditService;

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .active(true)
                .stock(request.getStock())
                .build();

        productRepository.save(product);

        auditService.log("Producto creado: " + product.getName(), product.getId().toString(), "CREATE", "SYSTEM",
                "Detalles de la creación");
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
        auditService.log("Producto eliminado con id: " + id, id.toString(), "DELETE", "SYSTEM",
                "Detalles de la eliminación");
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                applyDiscount(product.getPrice(), false),
                product.isActive(),
                product.getStock(),
                product.getCreatedAt());
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setActive(request.isActive());

        productRepository.save(product);
        auditService.log("Producto actualizado: " + product.getName(), product.getId().toString(), "UPDATE", "SYSTEM",
                "Detalles de la actualización");
        return mapToResponse(product);
    }

    private double applyDiscount(double price, boolean isFrequentClient) {
        LocalDateTime now = LocalDateTime.now();
        boolean isInRange = now.isAfter(discountStart) && now.isBefore(discountEnd);
        double finalPrice = price;

        if (isInRange) {
            finalPrice *= 0.9; // 10% de descuento general

            if (randomDiscountEnabled) {
                finalPrice *= 0.5; // 50% adicional si se marca como "aleatorio"
            }

            if (isFrequentClient) {
                finalPrice *= 0.95; // 5% adicional si es cliente frecuente
            }
        }

        return finalPrice;
    }

}
