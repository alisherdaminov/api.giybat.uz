package api.giybat.uz.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAdminFilterDTO {

    private String profileQuery; // name or username
    private String postQuery; // id or title
}
