package nmng108.microtube.mainservice.repository.projection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;
import org.springframework.lang.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class VideoWithChannelOwner extends Video {
    String channelPathname;
    String channelName;
    String channelAvatar;
    Long userId;
    @Nullable
    Long pausePosition;
    @Nullable
    Integer reaction;

    public VideoWithChannelOwner(Video other, String channelPathname, String channelName, String channelAvatar, Long userId, @Nullable Long pausePosition, @Nullable Integer reaction) {
        super(other);
        this.channelPathname = channelPathname;
        this.channelName = channelName;
        this.channelAvatar = channelAvatar;
        this.userId = userId;
        this.pausePosition = pausePosition;
        this.reaction = reaction;
    }
}
