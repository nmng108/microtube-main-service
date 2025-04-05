package nmng108.microtube.mainservice.dto.video.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@EqualsAndHashCode
@ToString
public class VisibilityDTO {
    int number;
    String code;
    String name;

    public VisibilityDTO(Video.Visibility visibility) {
        this.number = visibility.number;
        this.code = visibility.code;
        this.name = visibility.name;
    }
}
