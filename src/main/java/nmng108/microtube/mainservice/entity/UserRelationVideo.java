package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "USER_RELATION_VIDEO", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRelationVideo {
    @Column("USER_ID")
    long userId;
    @Column("VIDEO_ID")
    long videoId;
    @Column("PAUSE_POSITION")
    long pausePosition;
    @Column("REACTION")
    Integer reaction;
    @Column("CREATED_AT")
    @CreatedDate
    LocalDateTime createdAt;
    @Column("MODIFIED_AT")
    @LastModifiedDate
    LocalDateTime modifiedAt;
}
