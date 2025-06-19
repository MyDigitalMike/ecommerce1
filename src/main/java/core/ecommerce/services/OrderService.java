package core.ecommerce.services;

import core.ecommerce.dto.OrderRequest;
import core.ecommerce.entity.Order;

public interface OrderService {

    Order createOrder(OrderRequest request, String userEmail);
}
