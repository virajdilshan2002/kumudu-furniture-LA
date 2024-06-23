package lk.ijse.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class OrderDetailDTO {
    private String orderId;
    private String furnId;
    private int qty;
    private double unitPrice;
}
