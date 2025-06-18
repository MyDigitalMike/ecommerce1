package core.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import core.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    List<Product> findByNameContainingIgnoreCase(String keyword);
}
