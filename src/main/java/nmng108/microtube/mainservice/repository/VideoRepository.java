package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Video;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface VideoRepository extends R2dbcRepository<Video, Long> {
    @Query("SELECT v.* FROM VIDEO v WHERE v.CODE = :code")
    Mono<Video> findByCode(String code);

//    @Query("SELECT v.* FROM VIDEO v JOIN CHANNEL c ON v.CHANNEL_ID = c.ID WHERE v.ID = :id AND c.USER_ID = :userId AND v.DELETED_AT IS NULL")
//    Mono<Video> findByIdAndOwnerId(long id, long userId);
//
//    @Query("SELECT v.* FROM VIDEO v JOIN CHANNEL c ON v.CHANNEL_ID = c.ID WHERE v.CODE = :code AND c.USER_ID = :userId AND v.DELETED_AT IS NULL")
//    Mono<Video> findByCodeAndOwnerId(String code, long userId);

    @Query("SELECT v.* FROM VIDEO v WHERE v.CHANNEL_ID = :channelId")
    Flux<Video> findByChannelId(long channelId);

    @Modifying
    @Query("UPDATE VIDEO SET DELETED_BY = :deletedBy, DELETED_AT = :deletedAt WHERE ID = :id")
    Mono<Boolean> softDeleteById(long id, long deletedBy, LocalDateTime deletedAt);
}
