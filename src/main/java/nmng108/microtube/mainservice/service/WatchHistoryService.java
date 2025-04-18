package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.watchhistory.WatchHistoryDTO;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface WatchHistoryService {
    Mono<PagingResponse<WatchHistoryDTO>> getAll(PagingRequest pagingRequest);

    Mono<Void> log(String videoId, long pausePosition);

    Mono<Void> update(long id, long pausePosition);

    Mono<Void> delete(Collection<Long> ids);
}
