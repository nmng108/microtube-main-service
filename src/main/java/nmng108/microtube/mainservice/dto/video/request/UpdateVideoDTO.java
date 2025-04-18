package nmng108.microtube.mainservice.dto.video.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.util.converter.JsonConverters;
import nmng108.microtube.mainservice.util.converter.VideoVisibilityConverters;
import org.springframework.util.StringUtils;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateVideoDTO {
    @NotNull
    @JsonDeserialize(using = JsonConverters.VideoUpdateTypeDeserializer.class)
    VideoUpdateType updateType;
    @Size(max = 255)
    String title;
    @Size(max = 3000)
    String description;
    @JsonDeserialize(using = VideoVisibilityConverters.Deserializer.class)
    Video.Visibility visibility;
    Boolean allowsComment;
    Boolean increasesView;

    public void applyPatchUpdatesToEntity(Video video) {
        Optional.ofNullable(title).map(String::strip).filter(StringUtils::hasText).ifPresent(video::setTitle);
        Optional.ofNullable(description).map(String::strip).filter(StringUtils::hasText).ifPresent(video::setDescription);
        Optional.ofNullable(visibility).ifPresent(video::setVisibility);
        Optional.ofNullable(allowsComment).ifPresent(video::setAllowsComment);
        Optional.ofNullable(increasesView).filter((i) -> i).ifPresent((i) -> video.setViewCount(video.getViewCount() + 1));
    }
}
