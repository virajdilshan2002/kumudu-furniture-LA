package lk.ijse.pos.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class CredentialDTO {
    private String userName;
    private String fullName;
    private String email;
    private String password;
}
