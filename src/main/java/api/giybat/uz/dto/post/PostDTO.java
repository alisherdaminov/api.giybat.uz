package api.giybat.uz.dto.post;

import api.giybat.uz.dto.profile.image.AttachDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {

    private String id;
    private String title;
    private String content;
    private AttachDTO photo;
    private LocalDateTime createdDate;
}
