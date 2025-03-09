package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtDTO {

    private Integer id;
    private List<ProfileRoleEnum> rolesList;
}
