package core.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import core.ecommerce.entity.Order;
import core.ecommerce.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o.user.email, COUNT(o) " +
            "FROM Order o GROUP BY o.user.email " +
            "ORDER BY COUNT(o) DESC")
    List<Object[]> findTop5FrequentClients(Pageable pageable);

    long countByUser(User user);

}
