package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Channel;
import nmng108.microtube.mainservice.repository.projection.ChannelWithPersonalSubscription;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ChannelRepository extends R2dbcRepository<Channel, Long> {
    @Query("""
        SELECT c.*, (s.USER_ID IS NOT NULL) subscribed
        FROM microtube.channel c
            LEFT JOIN channel_subscription s ON c.ID = s.CHANNEL_ID AND s.USER_ID = :userId
        WHERE (:name IS NULL OR MATCH(c.PATHNAME, c.NAME, c.DESCRIPTION) AGAINST (:name))
          AND (:subscribed IS NULL OR IF(:subscribed, s.USER_ID IS NOT NULL, s.USER_ID IS NULL))
        ORDER BY IF(:name IS NULL, s.CREATED_AT, MATCH(c.PATHNAME, c.NAME, c.DESCRIPTION) AGAINST (:name)) DESC, c.CREATED_AT DESC
        LIMIT :size OFFSET :offset
        """)
    Flux<ChannelWithPersonalSubscription> searchByNameAndIsSubscribed(@Nullable String name, @Nullable Boolean subscribed, @Nullable Long userId, int size, long offset);

    @Query("""
        SELECT COUNT(c.ID)
        FROM microtube.channel c
            LEFT JOIN channel_subscription s ON c.ID = s.CHANNEL_ID AND s.USER_ID = :userId
        WHERE (:name IS NULL OR MATCH(c.PATHNAME, c.NAME, c.DESCRIPTION) AGAINST (:name))
          AND (:subscribed IS NULL OR IF(:subscribed, s.USER_ID IS NOT NULL, s.USER_ID IS NULL))
        """)
    Mono<Long> countSearchByNameOrPathname(@Nullable String name, @Nullable Boolean subscribed, @Nullable Long userId);

    @Query("SELECT c.* FROM microtube.channel c WHERE c.ID = :id OR c.PATHNAME = :id")
    Mono<Channel> findByIdOrPathname(long id);

    @Query("""
        SELECT c.*, (s.USER_ID IS NOT NULL) subscribed
        FROM microtube.channel c
            LEFT JOIN channel_subscription s ON c.ID = s.CHANNEL_ID AND s.USER_ID = :userId
        WHERE c.ID = :id OR c.PATHNAME = :id
        """)
    Mono<ChannelWithPersonalSubscription> findByIdOrPathname(long id, @Nullable Long userId);

    @Query("SELECT c.* FROM microtube.channel c WHERE c.PATHNAME = :pathname")
    Mono<Channel> findByPathname(String pathname);

    @Query("""
        SELECT c.*, (s.USER_ID IS NOT NULL) subscribed
        FROM microtube.channel c
            LEFT JOIN channel_subscription s ON c.ID = s.CHANNEL_ID AND s.USER_ID = :userId
        WHERE c.PATHNAME = :pathname
        """)
    Mono<ChannelWithPersonalSubscription> findByPathname(String pathname, @Nullable Long userId);

//    @Query("SELECT c.* FROM microtube.channel c WHERE c.USER_ID = :userId")
    Mono<Channel> findByUserId(long userId);

    // Error: Cannot decode class java.lang.Boolean for BIGINT
    // TODO: manually implement this or find another declarative way
//    @Query("SELECT EXISTS(SELECT 0 FROM microtube.channel c WHERE c.USER_ID = :userId AND c.DELETED_AT IS NULL)")
//    Mono<Boolean> existsByUserId(long userId);

    @Modifying
    @Query("UPDATE CHANNEL SET DELETED_BY = :deletedBy, DELETED_AT = :deletedAt WHERE ID = :id")
    Mono<Boolean> softDeleteById(long id, long deletedBy, LocalDateTime deletedAt);

    @Modifying
    @Query("INSERT INTO CHANNEL_SUBSCRIPTION(CHANNEL_ID, USER_ID) VALUES (:id, :userId)")
    Mono<Boolean> addSubscription(long id, long userId);

    @Modifying
    @Query("DELETE FROM CHANNEL_SUBSCRIPTION WHERE CHANNEL_ID = :id AND USER_ID = :userId")
    Mono<Boolean> removeSubscription(long id, long userId);

    @Modifying
    @Query("UPDATE CHANNEL SET SUBSCRIPTION_COUNT = SUBSCRIPTION_COUNT + 1 WHERE ID = :id")
    Mono<Boolean> increaseSubscriptionCount(long id);

    @Modifying
    @Query("UPDATE CHANNEL SET SUBSCRIPTION_COUNT = SUBSCRIPTION_COUNT - 1 WHERE ID = :id")
    Mono<Boolean> decreaseSubscriptionCount(long id);

    @Modifying
    @Query("UPDATE CHANNEL SET VIDEO_COUNT = VIDEO_COUNT + 1 WHERE ID = :id")
    Mono<Boolean> increaseVideoCount(long id);

    @Modifying
    @Query("UPDATE CHANNEL SET VIDEO_COUNT = VIDEO_COUNT - 1 WHERE ID = :id")
    Mono<Boolean> decreaseVideoCount(long id);
}
