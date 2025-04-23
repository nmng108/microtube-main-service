package nmng108.microtube.mainservice.repository.projection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Comment;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentWithUserInfo extends Comment {
    String username;
    String name; // user's full name
    String avatar;
    long childCount;
}
