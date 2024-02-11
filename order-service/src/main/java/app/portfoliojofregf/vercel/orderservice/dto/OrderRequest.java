package app.portfoliojofregf.vercel.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequest {
    private List<OrderLineItemsDto> orderlineitemslist;
}
