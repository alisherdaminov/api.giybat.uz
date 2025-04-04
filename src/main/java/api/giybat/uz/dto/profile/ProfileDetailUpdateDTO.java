package api.giybat.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDetailUpdateDTO {

    @NotBlank(message = "Name is required")
    private String name;
}
