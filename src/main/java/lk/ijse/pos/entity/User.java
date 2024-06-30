package lk.ijse.pos.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class User {
    private String userName;
    private String fullName;
    private String email;
    private String password;
}
