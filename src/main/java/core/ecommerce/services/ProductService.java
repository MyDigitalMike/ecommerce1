package core.ecommerce.services;

import java.util.List;

import core.ecommerce.dto.ProductRequest;
import core.ecommerce.dto.ProductResponse;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    List<ProductResponse> getAll();

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    List<ProductResponse> search(String query);

    List<ProductResponse> getActiveProducts();

    ProductResponse updateStock(Long productId, int newStock);

    ProductResponse getStockByProductId(Long productId);
    
    ProductResponse activateProduct(Long id);
}
