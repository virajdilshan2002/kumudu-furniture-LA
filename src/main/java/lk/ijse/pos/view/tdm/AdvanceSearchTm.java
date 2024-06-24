package lk.ijse.pos.view.tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class AdvanceSearchTm {
    private String furnId;
    private String furnDesc;
    private int qty;
    private double unitPrice;
    private double total;
}
