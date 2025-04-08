package api.giybat.uz.dto.profile.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachDTO {
    private String id;
    private String origenName;
    private String extension;
    private Long size;
    private String url;
    private LocalDateTime createdDate;

}
