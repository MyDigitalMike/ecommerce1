package core.ecommerce.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import core.ecommerce.services.ReportService;

import core.ecommerce.repository.OrderItemRepository;
import core.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ReportServiceImpl implements ReportService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Map<String, Object>> topSellingProducts() {
        return orderItemRepository.findTop5BestSellingProducts(PageRequest.of(0, 5))
                .stream()
                .map(obj -> Map.of(
                        "product", obj[0],
                        "totalSold", obj[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> topFrequentClients() {
        return orderRepository.findTop5FrequentClients(PageRequest.of(0, 5))
                .stream()
                .map(obj -> Map.of("email", obj[0], "ordersCount", obj[1]))
                .collect(Collectors.toList());
    }

}
