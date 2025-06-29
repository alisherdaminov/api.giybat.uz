package api.giybat.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUsernameUpdateDTO {

    @NotBlank(message = "Username is required")
    private String username;
}
