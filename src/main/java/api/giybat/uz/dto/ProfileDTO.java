package api.giybat.uz.dto;

import api.giybat.uz.dto.profile.image.AttachDTO;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)// null degan qiymatlarni olmaydi
public class ProfileDTO {

    private Integer id;
    private String name;
    private String username;
    private List<ProfileRoleEnum> rolesList;
    private String jwt;
    private AttachDTO attachDTO;
    private GeneralStatus status;
    private LocalDateTime createdDate;
    private Long postCount;
}


