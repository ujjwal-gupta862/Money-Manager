package Money.Manager.dto;

import lombok.Data;

@Data
public class AuthDTO {
    private String email;
    private String password;
    private String token;
}
