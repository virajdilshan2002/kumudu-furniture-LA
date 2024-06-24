package lk.ijse.pos.view.tdm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class FurnitureTm {
    private String furnId;
    private String furnDesc;
    private String furnWoodType;
    private String furnColor;
    private double furnPrice;
    private int furnQty;
    private JFXButton btnView;
}
