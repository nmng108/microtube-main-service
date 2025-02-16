package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.video.request.CreateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.UpdateVideoDTO;
import nmng108.microtube.mainservice.dto.video.response.VideoDTO;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.repository.ChannelRepository;
import nmng108.microtube.mainservice.repository.VideoRepository;
import nmng108.microtube.mainservice.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoServiceImpl implements VideoService {
    VideoRepository videoRepository;
    ChannelRepository channelRepository;

    @Override
    public Mono<BaseResponse<List<VideoDTO>>> getAllVideos() {
        return videoRepository.findAll()
                .map(VideoDTO::new)
                .collectList()
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<VideoDTO>> getVideoById(long id) {
        return videoRepository.findById(id).log()
                .map(VideoDTO::new)
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<VideoDTO>> createVideoInfo(CreateVideoDTO dto) {
        return Mono.just(dto.toVideo()).log()
                .filterWhen((v) -> channelRepository.existsById(v.getChannelId()))
                .switchIfEmpty(Mono.error(new BadRequestException("Channel does not exists")))
                .flatMap(videoRepository::save)
                .mapNotNull(VideoDTO::new)
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public void uploadVideo(long id, MultipartFile file) {

    }

    @Override
    public Mono<BaseResponse<VideoDTO>> updateVideoInfo(long id, UpdateVideoDTO dto) {
        return videoRepository.findById(id).log()
                .flatMap((v) -> {
                    Optional.ofNullable(dto.getName()).ifPresent(v::setName);
                    Optional.ofNullable(dto.getDescription()).ifPresent(v::setDescription);
                    Optional.ofNullable(dto.getVisibility()).ifPresent(v::setVisibility);

                    return videoRepository.save(v);
                })
                .mapNotNull(VideoDTO::new)
                .mapNotNull(BaseResponse::succeeded);
//                .(() -> new BadRequestException("Video not found"));
    }

    @Override
    public Mono<BaseResponse<Void>> deleteVideo(long id) {
        return videoRepository.findById(id).log()
                .flatMap(videoRepository::delete)
                .then(Mono.fromCallable(BaseResponse::succeeded));
    }
}
