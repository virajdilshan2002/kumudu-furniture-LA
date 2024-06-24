package lk.ijse.pos.view.tdm;
import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class CustomerTm {
    private String id;
    private String name;
    private String address;
    private String contact;
    private JFXButton Action;
}
