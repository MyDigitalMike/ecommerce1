package core.ecommerce.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import core.ecommerce.dto.OrderItemRequest;
import core.ecommerce.dto.OrderRequest;
import core.ecommerce.entity.Order;
import core.ecommerce.entity.OrderItem;
import core.ecommerce.entity.Product;
import core.ecommerce.entity.User;
import core.ecommerce.repository.OrderRepository;
import core.ecommerce.repository.ProductRepository;
import core.ecommerce.repository.UserRepository;
import core.ecommerce.services.AuditService;
import core.ecommerce.services.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuditService auditService;

    @Value("${discount.time.start}")
    private LocalDateTime discountStart;

    @Value("${discount.time.end}")
    private LocalDateTime discountEnd;

    @Override
    public Order createOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        order.setRandomDiscount(request.isRandomDiscount());

        double total = 0.0;
        List<OrderItem> items = new ArrayList<>();

        boolean isInRange = LocalDateTime.now().isAfter(discountStart) && LocalDateTime.now().isBefore(discountEnd);
        long ordersCount = orderRepository.countByUser(user);
        boolean isFrequentClient = ordersCount >= 5;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("No hay suficiente stock para el producto: " + product.getName());
            }
            product.setStock(product.getStock() - itemRequest.getQuantity());
            if (product.getStock() <= 0) {
                product.setActive(false); // Inactivar si se agotó
            }
            productRepository.save(product);
            double price = product.getPrice();
            if (isInRange) {
                price *= 0.9;
                if (request.isRandomDiscount())
                    price *= 0.5;
                if (isFrequentClient)
                    price *= 0.95;
            }

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(price)
                    .order(order)
                    .build();

            items.add(orderItem);
            total += price * itemRequest.getQuantity();
        }

        order.setTotal(total);
        order.setItems(items);

        Order saved = orderRepository.save(order);

        auditService.log("Orden creada", user.getEmail(), "CREATE", "ORDER", "Total: " + total);
        return saved;
    }
}
