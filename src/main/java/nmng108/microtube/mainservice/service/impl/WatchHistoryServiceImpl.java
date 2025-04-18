package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import nmng108.microtube.mainservice.dto.watchhistory.WatchHistoryDTO;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.entity.WatchHistory;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import nmng108.microtube.mainservice.repository.VideoRepository;
import nmng108.microtube.mainservice.repository.WatchHistoryRepository;
import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
import nmng108.microtube.mainservice.service.ObjectStoreService;
import nmng108.microtube.mainservice.service.UserService;
import nmng108.microtube.mainservice.service.VideoService;
import nmng108.microtube.mainservice.service.WatchHistoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WatchHistoryServiceImpl implements WatchHistoryService {
    ObjectStoreService objectStoreService;
    UserService userService;
    VideoService videoService;
    VideoRepository videoRepository;
    WatchHistoryRepository watchHistoryRepository;

    @Override
    public Mono<PagingResponse<WatchHistoryDTO>> getAll(PagingRequest pagingRequest) {
        Pageable pageable = pagingRequest.toPageable();

        return userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new))
                .zipWhen((u) -> watchHistoryRepository.countByUserId(u.getId()))
                .filter((tuple2) -> tuple2.getT2() > 0)
                // TODO: this may not a performant solution, and a single direct query to database should be better.
                .zipWhen((tuple2) -> watchHistoryRepository.findByUserId(tuple2.getT1().getId(), pageable.getPageSize(), pageable.getOffset()).collectList())
                .zipWhen(
                        (tuple2) -> videoRepository.buildQuery(SearchVideoDTO.builder()
                                        .ids(tuple2.getT2().stream().map(WatchHistory::getVideoId).toList())
                                        .build())
                                .find()
                                .collect(Collectors.toUnmodifiableMap(Video::getId, (v) -> v)),
                        (tuple2, videos) -> {
                            Long total = tuple2.getT1().getT2();
                            List<WatchHistory> historyRecords = tuple2.getT2();
                            List<WatchHistoryDTO> watchHistoryDTOs = historyRecords.stream()
                                    .map((record) -> {
                                        var v = videos.get(record.getVideoId());

                                        return new WatchHistoryDTO(record, v)
                                                .setUrl(objectStoreService.getDownloadUrl(v.getDestFilepath()))
                                                .setThumbnail(objectStoreService.getDownloadUrl(v.getThumbnail()))
                                                .setChannelAvatar(objectStoreService.getDownloadUrl(v.getChannelAvatar()));
                                    })
                                    .toList();

                            return new PagingResponse<>(pagingRequest, total, watchHistoryDTOs);
                        }
                )
                .switchIfEmpty(Mono.just(new PagingResponse<>()));
    }

    @Override
    public Mono<Void> log(String videoId, long pausePosition) {
        Mono<VideoWithChannelOwner> videoMono = videoService.retrieveVideo(videoId).switchIfEmpty(Mono.error(() -> new NoResourceFoundException("")));
        Mono<UserDetailsDTO> userMono = userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new));

        return Mono.zip(userMono, videoMono)

                .flatMap((tuple2) -> {
                    long userId = tuple2.getT1().getId();
                    long videoIdLong = tuple2.getT2().getId();

                    return watchHistoryRepository.findFirstInDayByUserIdAndVideoId(userId, videoIdLong, ZonedDateTime.now(ZoneOffset.UTC).toLocalDate())
                            .flatMap((r) -> {
                                r.setPausePosition(pausePosition);

                                return watchHistoryRepository.save(r);
                            })
                            .doOnNext((r) -> log.info("found rec " + r.getId()))
                            .switchIfEmpty(watchHistoryRepository.save(new WatchHistory(tuple2.getT1().getId(), tuple2.getT2().getId(), pausePosition)));
                })
                .then();
    }

    @Override
    public Mono<Void> update(long id, long pausePosition) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(Collection<Long> ids) {
        return userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new))
                .flatMap((u) -> watchHistoryRepository.deleteByUserIdAndIdIn(u.getId(), ids))
                .then();
    }
}
