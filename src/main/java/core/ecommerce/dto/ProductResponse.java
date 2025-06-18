package core.ecommerce.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private double finalPrice;
    private boolean active;
    private int stock;
    private LocalDateTime createdAt;
}
