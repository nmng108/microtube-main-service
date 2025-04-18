package nmng108.microtube.mainservice.dto.video.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoDTO {
    final Long id;
    final String code;
    final String title;
    @Nullable
    String description;
    @Nullable
    VisibilityDTO visibility;
    @Nullable
    StatusDTO status;
    @Nullable
    String thumbnail;
    @Nullable
    String url;
    final long viewCount;
    long likeCount;
    long dislikeCount;
    final Long channelId;
    @Nullable
    String channelName;
    @Nullable
    String channelPathname;
    @Nullable
    String channelAvatar;
    Long userId;
    final Long createdBy;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    final ZonedDateTime createdAt;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Nullable
    ZonedDateTime modifiedAt;
    @Nullable
    Long pausePosition;
    @Nullable
    Integer reaction;

    public VideoDTO(Video video, Format format) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.code = video.getCode();
        this.status = new StatusDTO(video.getStatus());
        this.viewCount = video.getViewCount();
        this.channelId = video.getChannelId();
        this.createdAt = video.getCreatedAt().atZone(ZoneOffset.UTC);
        this.createdBy = video.getCreatedBy();

        if (format == Format.DETAILS) {
            this.description = video.getDescription();
            this.visibility = new VisibilityDTO(video.getVisibility());
            this.likeCount = video.getLikeCount();
            this.dislikeCount = video.getDislikeCount();
            this.modifiedAt = video.getModifiedAt().atZone(ZoneOffset.UTC);
        }
    }

    public VideoDTO(VideoWithChannelOwner video, Format format) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.code = video.getCode();
        this.status = new StatusDTO(video.getStatus());
        this.viewCount = video.getViewCount();
        this.channelId = video.getChannelId();
        this.createdAt = video.getCreatedAt().atZone(ZoneOffset.UTC);
        this.createdBy = video.getCreatedBy();
        this.pausePosition = video.getPausePosition();
        this.reaction = video.getReaction();

        if (format == Format.DETAILS) {
            this.description = video.getDescription();
            this.visibility = new VisibilityDTO(video.getVisibility());
            this.likeCount = video.getLikeCount();
            this.dislikeCount = video.getDislikeCount();
            this.modifiedAt = video.getModifiedAt().atZone(ZoneOffset.UTC);
            this.channelName = video.getChannelName();
            this.channelPathname = video.getChannelPathname();
            this.userId = video.getUserId();
        }
    }

    public VideoDTO setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;

        return this;
    }

    /**
     * Automatically append master filename "master.m3u8" after received url.
     */
    public VideoDTO setUrl(String url) {
        this.url = url + "/master.m3u8";

        return this;
    }

    public VideoDTO setChannelAvatar(String channelAvatar) {
        this.channelAvatar = channelAvatar;

        return this;
    }


    public enum Format {
        CONCISE,
        DETAILS
    }
}
