package nmng108.microtube.mainservice.router;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import nmng108.microtube.mainservice.dto.watchhistory.WatchHistoryDTO;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.service.WatchHistoryService;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
@RequestMapping("${api.base-path}" + Routes.watchHistory)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryResource {
    String basePath;
    WatchHistoryService watchHistoryService;

    public WatchHistoryResource(@Value("${api.base-path}") String basePath, WatchHistoryService watchHistoryService) {
        this.basePath = basePath + Routes.Video.basePath;
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping
    public Mono<ResponseEntity<BaseResponse<PagingResponse<WatchHistoryDTO>>>> getAll(@Valid SearchVideoDTO dto) {
        return watchHistoryService.getAll(dto).map((d) -> ResponseEntity.ok(BaseResponse.succeeded(d)));
    }

    @PutMapping
    public Mono<ResponseEntity<BaseResponse<Void>>> log(@RequestBody @Valid WatchHistoryResource.CreateHistoryRecordDTO dto) {
        if (dto.getVideoId() == null) {
            throw new BadRequestException("videoId is required");
        }

        return watchHistoryService.log(dto.getVideoId(), dto.getPausePosition()).thenReturn(ResponseEntity.ok(BaseResponse.succeeded()));
    }

//    @PatchMapping("/{id}")
//    public Mono<ResponseEntity<BaseResponse<Void>>> update(@PathVariable("id") long id, CreateHistoryRecordDTO dto) {
//        return watchHistoryService.update(id, dto.getPausePosition()).thenReturn(ResponseEntity.ok(BaseResponse.succeeded()));
//    }

    @DeleteMapping
    public Mono<ResponseEntity<BaseResponse<Void>>> delete(@RequestParam("id") Collection<Long> ids) {
        return watchHistoryService.delete(ids).thenReturn(ResponseEntity.ok(BaseResponse.succeeded()));
    }

    @Getter
    public static final class CreateHistoryRecordDTO {
        private String videoId;
        @NotNull
        @Min(0)
        private Long pausePosition; // unit: second
    }
}
