package nmng108.microtube.mainservice.dto.video.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Video;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoDTO {
    long id;
    String name;
    String description;
    int visibility;
    long channelId;
    String channelName;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    long createdBy;
    LocalDateTime createdAt;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime modifiedAt;

    public VideoDTO(Video video) {
        this.id = video.getId();
        this.name = video.getName();
        this.description = video.getDescription();
        this.visibility = video.getVisibility();
        this.channelId = video.getChannelId();
        this.createdAt = video.getCreatedAt();
        this.createdBy = video.getCreatedBy();
        this.modifiedAt = video.getModifiedAt();
    }
}
