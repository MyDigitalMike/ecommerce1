package core.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private int stock;
    private boolean active;
}
