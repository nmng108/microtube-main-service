package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Channel;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ChannelRepository extends R2dbcRepository<Channel, Long> {
    @Query("SELECT c.* FROM microtube.channel c WHERE c.PATHNAME = TRIM(:pathName)")
    Mono<Channel> findByPathName(String pathName);

//    @Query("SELECT c.* FROM microtube.channel c WHERE c.USER_ID = :userId")
    Mono<Channel> findByUserId(long userId);

    // Error: Cannot decode class java.lang.Boolean for BIGINT
    // TODO: manually implement this or find another declarative way
//    @Query("SELECT EXISTS(SELECT 0 FROM microtube.channel c WHERE c.USER_ID = :userId AND c.DELETED_AT IS NULL)")
//    Mono<Boolean> existsByUserId(long userId);

    @Modifying
    @Query("UPDATE CHANNEL SET DELETED_BY = :deletedBy, DELETED_AT = :deletedAt WHERE ID = :id")
    Mono<Boolean> softDeleteById(long id, long deletedBy, LocalDateTime deletedAt);

}
