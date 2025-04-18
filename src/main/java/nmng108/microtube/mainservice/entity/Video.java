package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import nmng108.microtube.mainservice.util.converter.PersistentEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Table(name = "VIDEO", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Video extends Accountable implements Cloneable {
    @Id
    @Column("ID")
    long id;
    @Column("CODE")
    String code;
    @Column("TITLE")
    String title;
    @Column("DESCRIPTION")
    String description;
    @Column("VISIBILITY")
    Visibility visibility;
    @Column("THUMBNAIL")
    String thumbnail;
    @Column("ORIGINAL_FILENAME")
    String originalFilename;
    @Column("TEMP_FILEPATH")
    String tempFilepath;
    @Column("DEST_FILEPATH")
    String destFilepath;
    @Column("STATUS")
    Status status;
    @Column("ALLOW_COMMENT")
    boolean allowsComment;
    @Column("VIEW_COUNT")
    long viewCount;
    @Column("LIKE_COUNT")
    long likeCount;
    @Column("DISLIKE_COUNT")
    long dislikeCount;
    @Column("CHANNEL_ID")
    long channelId;

    public Video(Video other) {
        super(other);
        this.id = other.id;
        this.code = other.code;
        this.title = other.title;
        this.description = other.description;
        this.visibility = other.visibility;
        this.thumbnail = other.thumbnail;
        this.originalFilename = other.originalFilename;
        this.tempFilepath = other.tempFilepath;
        this.destFilepath = other.destFilepath;
        this.status = other.status;
        this.allowsComment = other.allowsComment;
        this.viewCount = other.viewCount;
        this.likeCount = other.likeCount;
        this.dislikeCount = other.dislikeCount;
        this.channelId = other.channelId;
    }

    //    public Visibility getVisibility() {
//        return VideoVisibilityConverters.PersistenceConverter.getInstance().convertToEntityAttribute(visibility);
//    }

//    public void setVisibility(Visibility visibility) {
//        this.visibility = visibility.number;
//    }
//
//    public Status getStatus() {
//        return Arrays.stream(Status.values()).filter((s) -> s.number == status).findFirst().orElseThrow(
//                () -> new RuntimeException("No status found for " + status)
//        );
//    }

//    public void setStatus(Status status) {
//        this.status = status.number;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video o1 = (Video) o;
        return id != 0 && o1.id != 0 && id == o1.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Video(this);
    }

    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    @Getter
    public enum Visibility implements PersistentEnum<Integer> {
        PRIVATE(1, "PRIVATE", "Private"),
        NOT_LISTED(2, "NOT_LISTED", "Not listed"),
        PUBLIC(3, "PUBLIC", "Public");

        int number;
        String code;
        String name;

        Visibility(int number, String code, String name) {
            this.number = number;
            this.code = code;
            this.name = name;
        }

        @Nullable
        public static Visibility valueOf(int number) {
            return Arrays.stream(values()).filter((v) -> v.number == number).findFirst().orElse(null);
        }

        @Override
        public Integer getPersistedValue() {
            return number;
        }
    }

    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    @Getter
    public enum Status implements PersistentEnum<Integer> {
        CREATING(0, "CREATING", "Creating"),
        CREATED(1, "CREATED", "Created"),
        PROCESSING(2, "PROCESSING", "Processing"),
        READY(3, "READY", "Ready"),
        FAILED(4, "FAILED", "Failed");

        int number;
        String code;
        String name;

        Status(int number, String code, String name) {
            this.number = number;
            this.code = code;
            this.name = name;
        }

        @Nullable
        public static Status valueOf(int number) {
            return Arrays.stream(values()).filter((v) -> v.number == number).findFirst().orElse(null);
        }

        @Override
        public Integer getPersistedValue() {
            return number;
        }
    }
}
