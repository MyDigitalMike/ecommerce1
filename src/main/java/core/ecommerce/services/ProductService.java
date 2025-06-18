package core.ecommerce.services;

import java.util.List;

import core.ecommerce.dto.ProductRequest;
import core.ecommerce.dto.ProductResponse;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    List<ProductResponse> getAll();

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    List<ProductResponse> search(String keyword);
}
