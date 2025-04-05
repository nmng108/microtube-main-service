package nmng108.microtube.mainservice.dto.video.response;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@EqualsAndHashCode
@ToString
public class StatusDTO {
    int number;
    String code;
    String name;

    public StatusDTO(Video.Status status) {
        this.number = status.number;
        this.code = status.code;
        this.name = status.name;
    }
}
