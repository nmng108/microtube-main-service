package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.comment.CommentDTO;
import nmng108.microtube.mainservice.dto.comment.CreateCommentDTO;
import nmng108.microtube.mainservice.entity.Comment;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.exception.ForbiddenException;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import nmng108.microtube.mainservice.repository.CommentRepository;
import nmng108.microtube.mainservice.repository.projection.CommentWithUserInfo;
import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
import nmng108.microtube.mainservice.service.CommentService;
import nmng108.microtube.mainservice.service.ObjectStoreService;
import nmng108.microtube.mainservice.service.UserService;
import nmng108.microtube.mainservice.service.VideoService;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    ObjectStoreService objectStoreService;
    UserService userService;
    VideoService videoService;
    CommentRepository commentRepository;

    @Override
    public Mono<BaseResponse<PagingResponse<CommentDTO>>> getAll(String videoId, @Nullable Integer parentId, PagingRequest pagingRequest) {
        Pageable pageable = pagingRequest.toPageable();

        return videoService.retrieveVideo(videoId)
                .flatMap((v) -> commentRepository.findByVideoId(v.getId(), parentId, pageable.getPageSize(), pageable.getOffset())
                        .map((c) -> new CommentDTO(c, objectStoreService.getDownloadUrl(c.getAvatar())))
                        .collectList()
                        .zipWith(commentRepository.countByVideoId(v.getId(), parentId)))
                .map((tuple2) -> new PagingResponse<>(pagingRequest, tuple2.getT2(), tuple2.getT1()))
                .switchIfEmpty(Mono.just(new PagingResponse<>()))
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<CommentDTO>> get(long id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NoResourceFoundException("")))
                .map((c) -> BaseResponse.succeeded(new CommentDTO(c, objectStoreService.getDownloadUrl(c.getAvatar()))));
    }

    @Override
    public Mono<BaseResponse<CommentDTO>> create(CreateCommentDTO dto) {
        Mono<VideoWithChannelOwner> videoMono = videoService.retrieveVideo(dto.getVideoId())
                .switchIfEmpty(Mono.error(() -> new NoResourceFoundException("")))
                .filter(Video::isAllowsComment)
                .switchIfEmpty(Mono.error(ForbiddenException::new));
        Mono<UserDetailsDTO> userMono = userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new));

        return Mono.zip(userMono, videoMono)
                .flatMap((tuple2) -> {
                    Comment comment = dto.toComment();

                    comment.setUserId(tuple2.getT1().getId());
                    comment.setVideoId(tuple2.getT2().getId());

                    return commentRepository.save(comment);
                })
                .map((c) -> BaseResponse.succeeded(new CommentDTO(c)));
    }

    @Override
    public Mono<BaseResponse<CommentDTO>> update(long id, String content) {
        Mono<CommentWithUserInfo> commentMono = commentRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NoResourceFoundException("")))
                .filterWhen((c) -> videoService.retrieveVideo(String.valueOf(c.getVideoId())) // what if this returns empty Publisher?
                        .switchIfEmpty(Mono.error(ForbiddenException::new))
                        .map(Video::isAllowsComment))
                .switchIfEmpty(Mono.error(ForbiddenException::new));
        Mono<UserDetailsDTO> userMono = userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new));

        return Mono.zip(userMono, commentMono)
                .flatMap((tuple2) -> {
                    Comment comment = tuple2.getT2();

                    if (tuple2.getT1().getId() != comment.getUserId()) {
                        throw new ForbiddenException();
                    }

                    comment.setContent(content);

                    return commentRepository.save(comment);
                })
                .map((c) -> BaseResponse.succeeded(new CommentDTO(c)));
    }

    @Override
    @Transactional
    public Mono<BaseResponse<Void>> delete(long id) {
        Mono<CommentWithUserInfo> commentMono = commentRepository.findById(id).switchIfEmpty(Mono.error(() -> new NoResourceFoundException("")));
        Mono<UserDetailsDTO> userMono = userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new));

        return Mono.zip(userMono, commentMono)
                .flatMap((tuple2) -> {
                    Comment comment = tuple2.getT2();

                    if (tuple2.getT1().getId() != comment.getUserId()) {
                        return Mono.error(new ForbiddenException());
                    }

                    return commentRepository.deleteByIdRecursively(comment.getId());
                })
                .thenReturn(BaseResponse.succeeded());
    }
}
