package nmng108.microtube.mainservice.dto.video.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.util.converter.VideoVisibilityConverters;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class SearchVideoDTO extends PagingRequest {
    List<Long> ids;
    List<String> codes;
    String name; // name, desc, hashtag...
    @JsonDeserialize(using = VideoVisibilityConverters.Deserializer.class)
    Video.Visibility visibility;
    List<String> resolutions;
    Video.Status status;
    Long channelId;
    @Setter
    Long userIdWithRelation;
    Integer reaction;
}
