package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.WatchHistory;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collection;

public interface WatchHistoryRepository extends R2dbcRepository<WatchHistory, Long> {
    @Query("SELECT * FROM WATCH_HISTORY WHERE USER_ID = :userId ORDER BY CREATED_AT DESC LIMIT :size OFFSET :offset")
    Flux<WatchHistory> findByUserId(long userId, int size, long offset);

    Mono<Long> countByUserId(long userId);

    @Query("""
        SELECT * FROM (
            SELECT * FROM WATCH_HISTORY
            WHERE USER_ID = :userId AND DATE(CREATED_AT) = :date
            ORDER BY CREATED_AT DESC LIMIT 1
        ) t
        WHERE t.VIDEO_ID = :videoId
        """)
    Mono<WatchHistory> findFirstInDayByUserIdAndVideoId(long userId, long videoId, LocalDate date);

    Mono<Integer> deleteByUserIdAndIdIn(long userId, Collection<Long> ids);
}
