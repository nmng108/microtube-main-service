package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Objects;

@Table(name = "WATCH_HISTORY", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistory {
    @Id
    long id;

    @Column("USER_ID")
    Long userId;
    @Column("VIDEO_ID")
    Long videoId;
    @Column("PAUSE_POSITION")
    Long pausePosition;
    @Column("CREATED_AT")
    Instant createdAt;
    @Column("MODIFIED_AT")
    Instant modifiedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchHistory that = (WatchHistory) o;
        return id != 0 && that.id != 0 && id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
