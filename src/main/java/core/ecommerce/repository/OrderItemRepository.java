package core.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import core.ecommerce.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi.product.name, SUM(oi.quantity) " +
            "FROM OrderItem oi GROUP BY oi.product.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTop5BestSellingProducts(Pageable pageable);

}
