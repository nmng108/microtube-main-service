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
import nmng108.microtube.mainservice.util.converter.VideoVisibilityConverters;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVideoDTO {
    @NotBlank
    @Size(max = 255)
    String title;
    @Size(max = 3000)
    String description;
    @NotNull
    @JsonDeserialize(using = VideoVisibilityConverters.Deserializer.class)
    Video.Visibility visibility;
    boolean allowsComment;

    public Video toVideo() {
        return Video.builder()
                .title(title.strip())
                .description(description.strip().replaceAll("<", "&lt;").replaceAll(">", "&gt;"))
                .visibility(visibility)
                .allowsComment(allowsComment)
                .build();
    }
}
