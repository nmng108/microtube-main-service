package nmng108.microtube.mainservice.repository.projection;

import nmng108.microtube.mainservice.entity.Video;

public class VideoWithChannelOwner extends Video {
    String channelPath;
    String channelName;
    String userId;
}
