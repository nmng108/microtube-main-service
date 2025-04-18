package nmng108.microtube.mainservice.dto.comment;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Comment;
import nmng108.microtube.mainservice.repository.projection.CommentWithUserInfo;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {
    long id;
    long userId;
    String username;
    String name;
    String avatar;
    long videoId;
    Long parentId;
    int level;
    String content;
    long likeCount;
    long dislikeCount;
    ZonedDateTime createdAt;

    public CommentDTO(CommentWithUserInfo comment, @Nullable String avatar) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.username = comment.getUsername();
        this.name = comment.getName();
        this.avatar = avatar;
        this.videoId = comment.getVideoId();
        this.parentId = comment.getParentId();
        this.level = comment.getLevel();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.dislikeCount = comment.getDislikeCount();
        this.createdAt = comment.getCreatedAt().atZone(ZoneOffset.UTC);
    }

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.videoId = comment.getVideoId();
        this.parentId = comment.getParentId();
        this.level = comment.getLevel();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.dislikeCount = comment.getDislikeCount();
        this.createdAt = comment.getCreatedAt().atZone(ZoneOffset.UTC);
    }
}
