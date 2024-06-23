package lk.ijse.pos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class OrderDetail {
    private String orderId;
    private String furnId;
    private int qty;
    private double unitPrice;
}
