package api.giybat.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmDTO {
    @NotBlank(message = "Username required")
    private String username;  // -> Email or Phone number
    @NotBlank(message = "Confirmation code required")
    private String confirmationCode;
    @NotBlank(message = "Password required")
    private String password;
}
