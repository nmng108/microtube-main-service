package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.repository.extend.ExtendedVideoRepository;
import nmng108.microtube.mainservice.entity.UserRelationVideo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface VideoRepository extends R2dbcRepository<Video, Long>, ExtendedVideoRepository {
    @Query("""
        SELECT v.* FROM VIDEO v
        WHERE v.CHANNEL_ID = :channelId AND v.VISIBILITY = 1 -- only show public ones
        ORDER BY v.CREATED_AT DESC, v.MODIFIED_AT DESC LIMIT :size OFFSET :offset
        """)
    Flux<Video> findByChannelId(long channelId);

    @Query("SELECT v.* FROM VIDEO v WHERE v.ID = :id OR v.CODE = :id")
    Mono<Video> findByIdOrCode(long id);

    @Query("SELECT v.* FROM VIDEO v WHERE v.CODE = :code")
    Mono<Video> findByCode(String code);

//    @Query("SELECT v.* FROM VIDEO v JOIN CHANNEL c ON v.CHANNEL_ID = c.ID WHERE v.ID = :id AND c.USER_ID = :userId AND v.DELETED_AT IS NULL")
//    Mono<Video> findByIdAndOwnerId(long id, long userId);
//
//    @Query("SELECT v.* FROM VIDEO v JOIN CHANNEL c ON v.CHANNEL_ID = c.ID WHERE v.CODE = :code AND c.USER_ID = :userId AND v.DELETED_AT IS NULL")
//    Mono<Video> findByCodeAndOwnerId(String code, long userId);

    @Modifying
    @Query("UPDATE VIDEO SET DELETED_BY = :deletedBy, DELETED_AT = :deletedAt WHERE ID = :id")
    Mono<Boolean> softDeleteById(long id, long deletedBy, LocalDateTime deletedAt);

    @Modifying
    @Query("UPDATE VIDEO SET VIEW_COUNT = VIEW_COUNT + 1 WHERE ID = :id")
    Mono<Boolean> increaseViewCount(long id);

    @Modifying
    @Query("UPDATE VIDEO SET LIKE_COUNT = LIKE_COUNT + IF(:increases, 1, -1) WHERE ID = :id")
    Mono<Boolean> changeLikeCount(long id, boolean increases);

    @Modifying
    @Query("UPDATE VIDEO SET DISLIKE_COUNT = DISLIKE_COUNT + IF(:increases, 1, -1) WHERE ID = :id")
    Mono<Boolean> changeDislikeCount(long id, boolean increases);


    @Modifying
    @Query("SELECT * FROM USER_RELATION_VIDEO WHERE USER_ID = :userId AND VIDEO_ID = :videoId")
    Mono<UserRelationVideo> findRelationToUser(long userId, long videoId);

    @Modifying
    @Query("INSERT INTO USER_RELATION_VIDEO(USER_ID, VIDEO_ID, PAUSE_POSITION, REACTION, CREATED_AT) VALUES (:userId, :videoId, :pausePosition, :reactionNumber, :createdAt)")
    Mono<Boolean> insertRelationToUser(long userId, long videoId, long pausePosition, @Nullable Integer reactionNumber,  LocalDateTime createdAt);

    @Modifying
    @Query("UPDATE USER_RELATION_VIDEO SET PAUSE_POSITION = :pausePosition, REACTION = :reactionNumber, MODIFIED_AT = :modifiedAt WHERE USER_ID = :userId AND VIDEO_ID = :videoId")
    Mono<Boolean> updateRelationToUser(long userId, long videoId, long pausePosition, @Nullable Integer reactionNumber, LocalDateTime modifiedAt);
}
