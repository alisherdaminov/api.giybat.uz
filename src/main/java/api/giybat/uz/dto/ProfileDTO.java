package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileDTO {

    private Integer id;
    private String name;
    private String username;
    private List<ProfileRoleEnum> rolesList;
    private String jwt;
}


