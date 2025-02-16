package nmng108.microtube.mainservice.dto.channel.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Channel;
import nmng108.microtube.mainservice.entity.Video;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelDTO {
    long id;
    String name;
    String pathName;
    String description;
    long userId;
    long createdBy;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public ChannelDTO(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.pathName = channel.getPathName();
        this.description = channel.getDescription();
        this.userId = channel.getUserId();
        this.createdAt = channel.getCreatedAt();
        this.createdBy = channel.getCreatedBy();
        this.modifiedAt = channel.getModifiedAt();
    }
}
