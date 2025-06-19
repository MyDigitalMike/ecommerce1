package core.ecommerce.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import core.ecommerce.services.ReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> topProducts() {
        return ResponseEntity.ok(reportService.topSellingProducts());
    }

    @GetMapping("/top-clients")
    public ResponseEntity<List<Map<String, Object>>> topClients() {
        return ResponseEntity.ok(reportService.topFrequentClients());
    }

    
}
