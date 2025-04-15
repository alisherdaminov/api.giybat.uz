package api.giybat.uz.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarPostListDTO {

    @NotBlank(message = "Post id required")
    private String exceptPostId;
}
