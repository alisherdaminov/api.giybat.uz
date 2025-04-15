package api.giybat.uz.dto.post;

import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.profile.image.AttachDTO;
import api.giybat.uz.enums.GeneralStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)// null degan qiymatlarni olmaydi
public class PostDTO {

    private String id;
    private String title;
    private String content;
    private AttachDTO photo;
    private LocalDateTime createdDate;
    private ProfileDTO profile;
    private GeneralStatus status;
}
