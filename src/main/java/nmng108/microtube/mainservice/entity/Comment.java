package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "COMMENT", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    long id;
    @Column("USER_ID")
    long userId;
    @Column("VIDEO_ID")
    long videoId;
    @Column("PARENT_ID")
    Long parentId;
    @Column("LEVEL")
    int level;
    @Column("CONTENT")
    String content;
    @Column("LIKE_COUNT")
    long likeCount;
    @Column("DISLIKE_COUNT")
    long dislikeCount;

    @Column("CREATED_AT")
    @CreatedDate
    LocalDateTime createdAt;
    @Column("MODIFIED_BY")
    @LastModifiedBy
    Long modifiedBy;
    @Column("MODIFIED_AT")
    @LastModifiedDate
    LocalDateTime modifiedAt;
    @Column("DELETED_BY")
    private Long deletedBy;
    @Column("DELETED_AT")
    private LocalDateTime deletedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id != 0 && comment.id != 0 && id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
