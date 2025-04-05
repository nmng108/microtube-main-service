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
import org.springframework.util.StringUtils;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class UpdateVideoDTO {
    @Size(max = 255)
    String name;
    @Size(max = 3000)
    String description;
    @JsonDeserialize(using = ChannelVisibilityConverters.VisibilityDeserializer.class)
    Video.Visibility visibility;
    Boolean allowsComment;

    public void applyPatchUpdatesToEntity(Video video) {
        Optional.ofNullable(name).map(String::strip).filter(StringUtils::hasText).ifPresent(video::setName);
        Optional.ofNullable(description).map(String::strip).filter(StringUtils::hasText).ifPresent(video::setDescription);
        Optional.ofNullable(visibility).ifPresent(video::setVisibility);
        Optional.ofNullable(allowsComment).ifPresent(video::setAllowsComment);
    }
}
