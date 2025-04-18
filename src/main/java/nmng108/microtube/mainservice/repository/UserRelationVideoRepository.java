package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.UserRelationVideo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRelationVideoRepository extends R2dbcRepository<UserRelationVideo, Long> {
    Mono<UserRelationVideo> findByUserIdAndVideoId(Long userId, Long videoId);
}
