package nmng108.microtube.mainservice.dto.video.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.util.converter.ChannelVisibilityConverters;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVideoDTO {
    @NotBlank
    @Size(max = 255)
    String name;
    @Size(max = 3000)
    String description;
    @NotNull
    @JsonDeserialize(using = ChannelVisibilityConverters.VisibilityDeserializer.class)
    Video.Visibility visibility;
    boolean allowsComment;

    public Video toVideo() {
        return Video.builder()
                .name(name.strip())
                .description(description.strip())
                .visibility(visibility.number)
                .allowsComment(allowsComment)
                .build();
    }
}
