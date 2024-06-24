package lk.ijse.pos.view.tdm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class CartTm {
    private String itemId;
    private String description;
    private double unitPrice;
    private int qty;
    private double total;
    private JFXButton btnRemove;
}
