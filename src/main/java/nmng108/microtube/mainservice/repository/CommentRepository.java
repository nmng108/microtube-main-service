package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Comment;
import nmng108.microtube.mainservice.repository.projection.CommentWithUserInfo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends R2dbcRepository<Comment, Long> {
    @Query("""
        SELECT c.*, u.USERNAME, u.NAME, u.AVATAR, (SELECT COUNT(*) FROM COMMENT s_c WHERE s_c.PARENT_ID = c.ID) CHILD_COUNT
        FROM COMMENT c
            JOIN USER u ON c.USER_ID = u.ID
        WHERE VIDEO_ID = :videoId AND IF(:parentId IS NULL, LEVEL = 1, PARENT_ID = :parentId)
        ORDER BY c.CREATED_AT DESC
        LIMIT :size OFFSET :offset
        """)
    Flux<CommentWithUserInfo> findByVideoId(long videoId, @Nullable Integer parentId, int size, long offset);

    @Query("""
        SELECT COUNT(0)
        FROM COMMENT c
            JOIN USER u ON c.USER_ID = u.ID
        WHERE VIDEO_ID = :videoId AND IF(:parentId IS NULL, LEVEL = 1, PARENT_ID = :parentId)
        """)
    Mono<Long> countByVideoId(long videoId, @Nullable Integer parentId);

    @Query("""
        SELECT c.*, u.USERNAME username, u.NAME name, u.AVATAR avatar
        FROM COMMENT c
            JOIN USER u ON c.USER_ID = u.ID
        WHERE c.ID = :id
        """)
    Mono<CommentWithUserInfo> findById(long id);

    Mono<Boolean> deleteByVideoId(long videoId);

    @Modifying
    @Query("""
        WITH RECURSIVE COMMENT_TREE AS (
            SELECT * FROM COMMENT WHERE ID = :id
            UNION
            SELECT c.*
            FROM COMMENT c
                JOIN COMMENT_TREE ct ON C.PARENT_ID = ct.ID
        )
        DELETE FROM COMMENT WHERE ID IN (SELECT ID FROM COMMENT_TREE)
        """)
    Mono<Boolean> deleteByIdRecursively(long id);
}
