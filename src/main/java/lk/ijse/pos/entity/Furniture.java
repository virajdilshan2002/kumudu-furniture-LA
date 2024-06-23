package lk.ijse.pos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Furniture {
    private String furnId;
    private File imageFile;
    private String furnDescription;
    private String furnWoodType;
    private String furnColor;
    private double furnPrice;
    private int furnQty;
}
