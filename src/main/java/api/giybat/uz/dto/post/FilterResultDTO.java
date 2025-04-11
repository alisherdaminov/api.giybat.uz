package api.giybat.uz.dto.post;

import api.giybat.uz.entity.PostEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterResultDTO<T> {
    private List<T> postEntities;
    private Long count;

    public FilterResultDTO(List<T> postEntities, Long count) {
        this.postEntities = postEntities;
        this.count = count;
    }
}
