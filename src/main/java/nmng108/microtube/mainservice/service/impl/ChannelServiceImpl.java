package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.channel.request.CreateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.request.UpdateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.response.ChannelDTO;
import nmng108.microtube.mainservice.entity.Channel;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.repository.ChannelRepository;
import nmng108.microtube.mainservice.repository.UserRepository;
import nmng108.microtube.mainservice.service.ChannelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ChannelServiceImpl implements ChannelService {
    ChannelRepository channelRepository;
    UserRepository userRepository;

    @Override
    public Mono<BaseResponse<List<ChannelDTO>>> getAllChannels() {
        return channelRepository.findAll().log()
                .mapNotNull(ChannelDTO::new)
                .collectList()
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<ChannelDTO>> getChannelByIdOrPathName(String identifiable) {
//        return Mono.just(0).log() // version 1
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
        return retrieveChannel(identifiable)
                .mapNotNull(ChannelDTO::new)
                .mapNotNull(BaseResponse::succeeded)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")));
    }

    @Override
    public Mono<BaseResponse<ChannelDTO>> createChannelInfo(CreateChannelDTO dto) {
        return Mono.just(dto.toChannel()).log()
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
                .filterWhen((c) -> userRepository.existsById(c.getUserId())) // continue if user exists
                .switchIfEmpty(Mono.error(new BadRequestException("User does not exist")))
                .filterWhen((c) -> channelRepository.existsByUserId(c.getUserId()).map((channelHasExists) -> !channelHasExists)) // continue if no channel is found
                .switchIfEmpty(Mono.error(new BadRequestException("Cannot create new channel")))

                .flatMap((c) -> {
                    if (StringUtils.hasText(c.getPathName())) {
                        return channelRepository.save(c);
                    } else {
                        return userRepository.findUsernameById(c.getUserId())
                                .flatMap((username) -> {
                                    c.setPathName(username);

                                    return channelRepository.save(c);
                                });
                    }
                })
                .mapNotNull(ChannelDTO::new)
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<ChannelDTO>> updateChannelInfo(String identifiable, UpdateChannelDTO dto) {
        return retrieveChannel(identifiable)
                .flatMap((c) -> {
                    Optional.ofNullable(dto.getName()).ifPresent(c::setName);
                    Optional.ofNullable(dto.getDescription()).ifPresent(c::setDescription);
                    Optional.ofNullable(dto.getName()).ifPresent(c::setName);

                    return channelRepository.save(c);
                })
                .mapNotNull(ChannelDTO::new)
                .mapNotNull(BaseResponse::succeeded)
                ;
    }

    @Override
    public Mono<BaseResponse<Void>> deleteChannel(String identifiable) {
        return retrieveChannel(identifiable)
                .flatMap(channelRepository::delete)
                .then(Mono.fromCallable(BaseResponse::succeeded));
    }

    public Mono<Channel> retrieveChannel(String identifiable) {
        return Mono.fromCallable(() -> Long.parseLong(identifiable)).log()
                .flatMap(channelRepository::findById)
                .onErrorResume(NumberFormatException.class, (error) -> channelRepository.findByPathName(identifiable));
    }
}
