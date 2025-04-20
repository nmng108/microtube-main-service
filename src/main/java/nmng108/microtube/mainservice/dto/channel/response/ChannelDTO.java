package nmng108.microtube.mainservice.dto.channel.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Channel;
import nmng108.microtube.mainservice.repository.projection.ChannelWithPersonalSubscription;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelDTO {
    long id;
    String name;
    String pathname;
    String description;
    @Setter
    String avatar;
    long subscriptionCount;
    long videoCount;
    long userId;
    long createdBy;
    ZonedDateTime createdAt;
    @Nullable
    Boolean subscribed;

    public ChannelDTO(Channel channel, String avatarUrl) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.pathname = channel.getPathname();
        this.description = channel.getDescription();
        this.avatar = avatarUrl;
        this.subscriptionCount = channel.getSubscriptionCount();
        this.videoCount = channel.getVideoCount();
        this.userId = channel.getUserId();
        this.createdAt = channel.getCreatedAt().atZone(ZoneOffset.UTC);
        this.createdBy = channel.getCreatedBy();

        if (channel instanceof ChannelWithPersonalSubscription c) {
            subscribed = c.getSubscribed() != null && c.getSubscribed() > 0;
        }
    }
}
