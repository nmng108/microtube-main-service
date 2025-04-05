package nmng108.microtube.mainservice.dto.video.response;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class VideoDTO {
    long id;
    String name;
    String code;
    String description;
    VisibilityDTO visibility;
    StatusDTO status;
    long channelId;
//    String channelName;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    long createdBy;
    LocalDateTime createdAt;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime modifiedAt;

    public VideoDTO(Video video) {
        this.id = video.getId();
        this.name = video.getName();
        this.code = video.getCode();
        this.description = video.getDescription();
        this.visibility = new VisibilityDTO(video.getVisibility());
        this.status = new StatusDTO(video.getStatus());
        this.channelId = video.getChannelId();
        this.createdAt = video.getCreatedAt();
        this.createdBy = video.getCreatedBy();
        this.modifiedAt = video.getModifiedAt();
    }
}
