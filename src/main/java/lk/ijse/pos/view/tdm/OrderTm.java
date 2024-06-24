package lk.ijse.pos.view.tdm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class OrderTm {
    private String orderId;
    private String cusId;
    private String orderDate;
    private String paymentType;
    private double totalPayment;
    private JFXButton Details;
}
