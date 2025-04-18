package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Channel;
import nmng108.microtube.mainservice.entity.Comment;
import nmng108.microtube.mainservice.repository.projection.CommentWithUserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CommentRepository extends R2dbcRepository<Comment, Long> {
    @Query("""
        SELECT c.*, u.USERNAME username, u.NAME name, u.AVATAR avatar
        FROM COMMENT c
            JOIN USER u ON c.USER_ID = u.ID
        WHERE VIDEO_ID = :videoId
        ORDER BY c.CREATED_AT DESC
        LIMIT :size OFFSET :offset
        """)
    Flux<CommentWithUserInfo> findByVideoId(long videoId, int size, long offset);

    @Query("""
        SELECT c.*, u.USERNAME username, u.NAME name, u.AVATAR avatar
        FROM COMMENT c
            JOIN USER u ON c.USER_ID = u.ID
        WHERE VIDEO_ID = :videoId
        """)
    Mono<CommentWithUserInfo> findById(long videoId);

    Mono<Long> countByVideoId(long videoId);
}
