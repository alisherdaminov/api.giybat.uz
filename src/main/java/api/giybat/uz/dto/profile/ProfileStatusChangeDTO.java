package api.giybat.uz.dto.profile;

import api.giybat.uz.enums.GeneralStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileStatusChangeDTO {

    private GeneralStatus status;
}
