package lk.ijse.pos.dto;

import lk.ijse.pos.util.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class OrderDTO {
    private String orderId;
    private String cusId;
    private String orderDate;
    private PaymentType paymentType;
    private Double advancedPayment;
    private Double totalPayment;
}
