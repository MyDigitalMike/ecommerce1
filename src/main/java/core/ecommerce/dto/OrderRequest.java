package core.ecommerce.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Data
@AllArgsConstructor
@Getter
@Setter
public class OrderRequest {
    private List<OrderItemRequest> items;
    private boolean randomDiscount;
}
