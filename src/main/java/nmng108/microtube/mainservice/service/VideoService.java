package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.video.request.CreateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.UpdateVideoDTO;
import nmng108.microtube.mainservice.dto.video.response.VideoDTO;
import nmng108.microtube.mainservice.entity.Video;
import org.springframework.core.io.Resource;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VideoService {
    Mono<BaseResponse<List<VideoDTO>>> getAll();

    Mono<BaseResponse<VideoDTO>> getById(String id);

    Mono<BaseResponse<VideoDTO>> create(CreateVideoDTO video);

    /**
     * Fetch master file
     */
    Mono<Resource> getMasterFile(String id);

    Mono<Resource> getResolutionBasedFile(String id, String resolution, String filename);

//    Mono<BaseResponse<Void>> uploadVideo(long id, Mono<FilePart> file);

    Mono<BaseResponse<VideoDTO>> updateInfo(String id, UpdateVideoDTO video);

    Mono<BaseResponse<Void>> delete(String id);

    Mono<Void> delete(Video video, UserDetailsDTO user);
}