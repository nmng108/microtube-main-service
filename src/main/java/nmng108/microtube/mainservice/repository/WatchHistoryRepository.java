package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.WatchHistory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface WatchHistoryRepository extends R2dbcRepository<WatchHistory, Long> {
    Mono<WatchHistory> findByUserId(long userId);
}
