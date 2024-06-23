package lk.ijse.pos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Order {
    private String orderId;
    private String cusId;
    private String orderDate;
    private String paymentType;
    private Double advancedPayment;
    private Double totalPayment;
}
