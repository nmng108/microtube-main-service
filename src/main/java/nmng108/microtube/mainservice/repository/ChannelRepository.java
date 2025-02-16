package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Channel;
import reactor.core.publisher.Mono;

public interface ChannelRepository extends SoftDeletionReactiveRepository<Channel, Long> {
    Mono<Channel> findByPathName(String pathName);

    Mono<Boolean> existsByUserId(long userId);
}
