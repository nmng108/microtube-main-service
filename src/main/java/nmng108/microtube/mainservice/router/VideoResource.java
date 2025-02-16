package nmng108.microtube.mainservice.router;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.video.request.CreateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.UpdateVideoDTO;
import nmng108.microtube.mainservice.dto.video.response.VideoDTO;
import nmng108.microtube.mainservice.service.VideoService;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}" + Routes.Video.basePath)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoResource {
    String basePath;
    VideoService videoService;

    public VideoResource(@Value("${api.base-path}") String basePath, VideoService videoService) {
        this.basePath = basePath + Routes.Video.basePath;
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<Mono<BaseResponse<List<VideoDTO>>>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<VideoDTO>>> getVideoById(@PathVariable long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @PostMapping
    public Mono<ResponseEntity<BaseResponse<VideoDTO>>> createVideo(@RequestBody @Valid CreateVideoDTO dto) {
        return videoService.createVideoInfo(dto).log()
                .map((res) -> {
                    long id = res.getData().getId();

                    return ResponseEntity.created(URI.create(STR."\{basePath}/\{id}")).body(res);
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<VideoDTO>>> updateVideo(@PathVariable("id") long id, @RequestBody @Valid UpdateVideoDTO dto) {
        return ResponseEntity.ok(videoService.updateVideoInfo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<Void>>> updateVideo(@PathVariable("id") long id) {
        return ResponseEntity.ok(videoService.deleteVideo(id));
    }
}
