package nmng108.microtube.mainservice.dto.watchhistory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.WatchHistory;
import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
import org.springframework.lang.Nullable;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class WatchHistoryDTO {
    final long id;
    long pausePosition;
    Long videoId;
    String code;
    String title;
    String thumbnail;
    String url;
    long viewCount;
    long likeCount;
    long dislikeCount;
    Long channelId;
    String channelName;
    String channelPathname;
    String channelAvatar;
    final Instant createdAt;

    public WatchHistoryDTO(WatchHistory watchHistory, @Nullable VideoWithChannelOwner video) {
        this.id = watchHistory.getId();
        this.pausePosition = watchHistory.getPausePosition();
        this.createdAt = watchHistory.getCreatedAt();//.atOffset(ZoneOffset.UTC).toLocalDateTime();

        if (video != null) {
            this.videoId = video.getId();
            this.code = video.getCode();
            this.title = video.getTitle();
            this.viewCount = video.getViewCount();
            this.likeCount = video.getLikeCount();
            this.dislikeCount = video.getDislikeCount();
            this.channelId = video.getChannelId();
            this.channelName = video.getChannelName();
            this.channelPathname = video.getChannelPathname();
        }
    }

    public WatchHistoryDTO setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;

        return this;
    }

    /**
     * Automatically append master filename "master.m3u8" after received url.
     */
    public WatchHistoryDTO setUrl(String url) {
        this.url = url + "/master.m3u8";

        return this;
    }

    public WatchHistoryDTO setChannelAvatar(String channelAvatar) {
        this.channelAvatar = channelAvatar;

        return this;
    }
}
