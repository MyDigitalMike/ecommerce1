package core.ecommerce.services;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String, Object>> topSellingProducts();
    List<Map<String, Object>> topFrequentClients();
}
