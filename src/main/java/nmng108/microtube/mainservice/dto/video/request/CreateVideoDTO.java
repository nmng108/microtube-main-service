package nmng108.microtube.mainservice.dto.video.request;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVideoDTO {
    String name;
    String description;
    int visibility;
    long channelId;

    public Video toVideo() {
        return Video.builder()
                .name(name)
                .description(description)
                .visibility(visibility)
                .channelId(channelId)
                .build();
    }
}
