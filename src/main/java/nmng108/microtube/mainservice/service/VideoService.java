package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.video.request.CreateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.UpdateVideoDTO;
import nmng108.microtube.mainservice.dto.video.response.VideoDTO;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VideoService {
    Mono<BaseResponse<List<VideoDTO>>> getAllVideos();

    Mono<BaseResponse<VideoDTO>> getVideoById(long id);

    Mono<BaseResponse<VideoDTO>> createVideoInfo(CreateVideoDTO video);

    void uploadVideo(long id, MultipartFile file);

    Mono<BaseResponse<VideoDTO>> updateVideoInfo(long id, UpdateVideoDTO video);

    Mono<BaseResponse<Void>> deleteVideo(long id);
}