package nmng108.microtube.mainservice.dto.video.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.util.converter.VideoVisibilityConverters;
import org.springframework.lang.Nullable;

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
    Integer reaction;
    /**
     * Get videos from subscribed channels. If set, requires {@link SearchVideoDTO#accessingUserId} is not null.
     */
    Boolean subscribed;

    SortedField sort;

    // The following fields are Intended to be set internally; should not be read from API request
    @Setter
    @JsonIgnore
    Long accessingUserId;
    @Setter
    @JsonIgnore
    List<Integer> allowedVisibility;

    public enum SortedField {
        CREATED_DATE,
        VIEW;

        @Nullable
        public static SortedField from(String name) {
            for (SortedField value : SortedField.values()) {
                if (value.name().equalsIgnoreCase(name)) {
                    return value;
                }
            }

            return null;
        }
    }
}
