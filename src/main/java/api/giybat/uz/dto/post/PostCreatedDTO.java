package api.giybat.uz.dto.post;

import api.giybat.uz.dto.profile.image.AttachCreatedDTO;
import api.giybat.uz.dto.profile.image.AttachDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCreatedDTO {

    @NotBlank(message = "Title is required")
    @Length(min = 5,max = 255, message = "Title must be min-5 to max-255 characters")
    private String title;
    @NotBlank(message = "Content is required")
    private String content;
    @NotNull(message = "Photo is required")
    private AttachCreatedDTO photo;

}
