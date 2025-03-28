package nmng108.microtube.mainservice.router;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.video.request.CreateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.UpdateVideoDTO;
import nmng108.microtube.mainservice.dto.video.response.VideoDTO;
import nmng108.microtube.mainservice.service.VideoService;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}" + Routes.Video.basePath)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoResource {
    String basePath;
    VideoService videoService;

    public VideoResource(@Value("${api.base-path}") String basePath, VideoService videoService) {
        this.basePath = basePath + Routes.Video.basePath;
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<Mono<BaseResponse<List<VideoDTO>>>> getAll() {
        return ResponseEntity.ok(videoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<VideoDTO>>> getById(@PathVariable long id) {
        return ResponseEntity.ok(videoService.getById(id));
    }

    @PostMapping
    public Mono<ResponseEntity<BaseResponse<VideoDTO>>> create(@RequestBody @Valid CreateVideoDTO dto) {
        return videoService.create(dto).log()
                .map((res) -> {
                    long id = res.getData().getId();

                    return ResponseEntity.created(URI.create(STR."\{basePath}/\{id}")).body(res);
                });
    }

    @Operation(summary = "Fetch master/index file of specified video")
    @GetMapping(value = "/{id}/master.m3u8", produces = "application/vnd.apple.mpegurl"/*MediaType.APPLICATION_OCTET_STREAM_VALUE*/)
    public ResponseEntity<Mono<Resource>> getMasterFile(@PathVariable("id") long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=master.m3u8")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, STR."\{HttpHeaders.CONTENT_TYPE}, \{HttpHeaders.CONTENT_LENGTH}, \{HttpHeaders.CONTENT_DISPOSITION}")
                .body(videoService.getMasterFile(id));
    }

    @Operation(summary = "Fetch segment file of specified video")
    @GetMapping(value = "/{id}/{resolution:^\\d{3,4}p$}/{filename:^\\w+\\.\\w{2,4}$}", produces = "application/vnd.apple.mpegurl"/*MediaType.APPLICATION_OCTET_STREAM_VALUE*/)
    public Mono<ResponseEntity<Resource>> getMasterFile(
            @PathVariable("id") long id,
            @PathVariable("resolution") String resolution,
            @PathVariable("filename") @Pattern(regexp = "^((playlist\\.m3u8)|(\\w+\\.ts))$") String filename
    ) {
        return videoService.getResolutionBasedFile(id, resolution, filename)
                .map((resource) -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                        .header(HttpHeaders.CONTENT_DISPOSITION, STR."attachment; filename=\{resource.getFilename()}")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(", ", HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_LENGTH, HttpHeaders.CONTENT_DISPOSITION))
                        .body(resource));
    }

//    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Mono<BaseResponse<Void>>> uploadVideo(@PathVariable("id") long id, @RequestPart(name = "file") Mono<FilePart> file) {
//        return ResponseEntity.ok(videoService.uploadVideo(id, file));
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<VideoDTO>>> updateInfo(@PathVariable("id") long id, @RequestBody @Valid UpdateVideoDTO dto) {
        return ResponseEntity.ok(videoService.updateInfo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<Void>>> delete(@PathVariable("id") long id) {
        return ResponseEntity.ok(videoService.delete(id));
    }
}
