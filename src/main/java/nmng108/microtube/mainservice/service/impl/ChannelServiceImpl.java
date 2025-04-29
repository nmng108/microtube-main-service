package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.channel.request.ChannelAction;
import nmng108.microtube.mainservice.dto.channel.request.CreateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.request.UpdateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.response.ChannelDTO;
import nmng108.microtube.mainservice.entity.Channel;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import nmng108.microtube.mainservice.repository.ChannelRepository;
import nmng108.microtube.mainservice.repository.VideoRepository;
import nmng108.microtube.mainservice.repository.projection.ChannelWithPersonalSubscription;
import nmng108.microtube.mainservice.service.ChannelService;
import nmng108.microtube.mainservice.service.ObjectStoreService;
import nmng108.microtube.mainservice.service.UserService;
import nmng108.microtube.mainservice.service.VideoService;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ChannelServiceImpl implements ChannelService {
    ObjectStoreService objectStoreService;
    UserService userService;
    ChannelRepository channelRepository;
    VideoService videoService;
    VideoRepository videoRepository;

    @Override
    public Mono<BaseResponse<PagingResponse<ChannelDTO>>> getAll(PagingRequest pagingRequest, @Nullable String name, @Nullable Boolean subscribed) {
        Pageable pageable = pagingRequest.toPageable();

        return userService.getCurrentUser()
                .singleOptional()
                .flatMap((u) -> channelRepository.searchByNameAndIsSubscribed(name, subscribed, u.map(UserDetailsDTO::getId).orElse(null), pageable.getPageSize(), pageable.getOffset())
                        .mapNotNull((c) -> new ChannelDTO(c, objectStoreService.getDownloadUrl(c.getAvatar())))
                        .collectList()
                        .zipWith(channelRepository.countSearchByNameOrPathname(name, subscribed, u.map(UserDetailsDTO::getId).orElse(null))))
                .map((tuple2) -> new PagingResponse<>(pagingRequest, tuple2.getT2(), tuple2.getT1()))
                .switchIfEmpty(Mono.just(new PagingResponse<>()))
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<ChannelDTO>> getByIdOrPathName(String identifiable) {
//        return Mono.just(0) // version 1
//                .flatMap((ignored) -> {
//                    System.out.println(ignored);
//                    try {
//                        Long id = Long.parseLong(identifiable);
//
//                        return channelRepository.findById(id);
//                    } catch (NumberFormatException e) {
//                        return channelRepository.findByPathName(identifiable);
//                    }
//                })
        return retrieveExtendedChannel(identifiable)
                .mapNotNull((c) -> new ChannelDTO(c, objectStoreService.getDownloadUrl(c.getAvatar())))
                .mapNotNull(BaseResponse::succeeded)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")));
    }

    @Override
    public Mono<BaseResponse<ChannelDTO>> create(CreateChannelDTO dto) {
        return userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new))
                // version 1
//                .publishOn(Schedulers.boundedElastic())
//                .handle((c, sink) -> {
//                    if (Boolean.FALSE.equals(userRepository.existsById(c.getUserId()).block())) {
//                        sink.error(new BadRequestException("User does not exist"));
//                    } else {
//                        if (Boolean.TRUE.equals(channelRepository.existsByUserId(c.getUserId()).block())) {
//                            sink.error(new BadRequestException("Cannot create new channel"));
//                        } else {
//                            sink.next(c);
//                        }
//                    }
//                })
                // version 2
//                .zipWhen((c) -> userRepository.existsById(c.getUserId()))
//                .filter(Tuple2::getT2) // continue if user exists
//                .map(Tuple2::getT1)
//                .zipWhen((c) -> channelRepository.existsByUserId(c.getUserId()))
//                .filter((tuple) -> !tuple.getT2()) // continue if no channel is found
//                .map(Tuple2::getT1)
                // version 3
                .filterWhen((user) -> channelRepository.findByUserId(user.getId()).map((_) -> false).switchIfEmpty(Mono.just(true))) // continue if no channel is found
                .switchIfEmpty(Mono.error(new BadRequestException("Cannot create new channel. 1 channel exists.")))
                .map((user) -> {
                    var c = dto.toChannel();

                    c.setUserId(user.getId());

                    if (!StringUtils.hasText(c.getPathname())) {
                        c.setPathname(user.getUsername());
                    }

                    return c;
                })
                .filterWhen((c) -> channelRepository.findByPathname(c.getPathname()).map((_) -> false).switchIfEmpty(Mono.just(true)))
                .switchIfEmpty(Mono.error(new BadRequestException("Cannot create new channel. Pathname exists.")))
                .flatMap(channelRepository::save)
                .mapNotNull((c) -> new ChannelDTO(c, objectStoreService.getDownloadUrl(c.getAvatar())))
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<ChannelDTO>> update(String identifiable, UpdateChannelDTO dto) {
        return retrieveChannel(identifiable)
                .flatMap((c) -> {
                    Optional.ofNullable(dto.getName()).map(String::strip).filter(StringUtils::hasText).ifPresent(c::setName);
                    Optional.ofNullable(dto.getPathname()).map(String::strip).filter(StringUtils::hasText).ifPresent(c::setPathname); // TODO: check if this has existed
                    Optional.ofNullable(dto.getDescription()).map(String::strip).filter(StringUtils::hasText).ifPresent(c::setDescription);

                    return channelRepository.save(c);
                })
                .mapNotNull((c) -> new ChannelDTO(c, objectStoreService.getDownloadUrl(c.getAvatar())))
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<Void>> delete(String identifiable) {
        return retrieveChannel(identifiable)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .doOnNext(c -> log.info("found channel: ID={}, pathname={}, name={}", c.getId(), c.getPathname(), c.getName()))
                .flatMap((c) -> userService.getCurrentUser()
                        .flatMap((user) -> channelRepository.softDeleteById(c.getId(), user.getId(), ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()))
                        .thenReturn(c))
                .zipWith(userService.getCurrentUser())
                .publishOn(Schedulers.boundedElastic())
                .doOnNext((tuple2) -> {/*Mono.from(*/
                    Channel c = tuple2.getT1();
                    UserDetailsDTO u = tuple2.getT2();

                    videoRepository.findByChannelId(c.getId())
//                                        .parallel(10) // should be used for CPU-bound tasks
//                                        .runOn(Schedulers.newParallel("delete-channel-delete-videos", 10))
                            .subscribeOn(Schedulers.boundedElastic()) // should be used for IO tasks
                            .flatMap((v) -> videoService.delete(v, u))
                            .then() // video deletion tasks are considered to be successful if there's no emitted error
                            .then(Mono.fromRunnable(() -> channelRepository.delete(c).subscribeOn(Schedulers.boundedElastic()).subscribe()))
                            .doOnSuccess((_) -> log.info("Deleted channel - ID={}, pathname={}", c.getId(), c.getPathname()))
                            .doOnError((_) -> log.error("Cannot delete channel with ID={}", c.getId()))
                            .subscribe();
                })
                .thenReturn(BaseResponse.succeeded())
                ;
    }

    @Override
    @Transactional
    public Mono<BaseResponse<Void>> doAction(String identifiable, ChannelAction action) {
        Mono<UserDetailsDTO> userMono = userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new));
        Mono<Channel> channelMono = retrieveChannel(identifiable).switchIfEmpty(Mono.error(() -> new NoResourceFoundException("")));

        return Mono.zip(userMono, channelMono)
                .flatMap((tuple2) -> action == ChannelAction.SUBSCRIBE
                        ? channelRepository.addSubscription(tuple2.getT2().getId(), tuple2.getT1().getId())
                        .doOnError((e) -> e.printStackTrace())
                        .then(channelRepository.increaseSubscriptionCount(tuple2.getT2().getId()))
                        : channelRepository.removeSubscription(tuple2.getT2().getId(), tuple2.getT1().getId())
                        .then(channelRepository.decreaseSubscriptionCount(tuple2.getT2().getId()))
                )
                .doOnNext((result) -> log.info("result: " + result))
                .thenReturn(BaseResponse.succeeded());
    }

    public Mono<Channel> retrieveChannel(String identifiable) {
        return Mono.fromCallable(() -> Long.parseLong(identifiable))
                .flatMap(channelRepository::findByIdOrPathname)
                .onErrorResume(NumberFormatException.class, (error) -> channelRepository.findByPathname(identifiable));
    }

    public Mono<ChannelWithPersonalSubscription> retrieveExtendedChannel(String identifiable) {
        return userService.getCurrentUser()
                .singleOptional()
                .flatMap((userDetailsOptional) -> {
                    Long userId = userDetailsOptional.map(UserDetailsDTO::getId).orElse(null);

                    return Mono.fromCallable(() -> Long.parseLong(identifiable))
                            .flatMap((id) -> channelRepository.findByIdOrPathname(id, userId))
                            .onErrorResume(NumberFormatException.class, (error) -> channelRepository.findByPathname(identifiable, userId));
                });
    }
}
