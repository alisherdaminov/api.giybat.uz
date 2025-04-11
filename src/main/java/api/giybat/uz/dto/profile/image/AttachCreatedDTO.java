package api.giybat.uz.dto.profile.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttachCreatedDTO {
    @NotBlank(message = "Id is required")
    private String id;


}
