package nmng108.microtube.mainservice.router;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.comment.CommentDTO;
import nmng108.microtube.mainservice.dto.comment.CreateCommentDTO;
import nmng108.microtube.mainservice.service.CommentService;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;


@RestController
@RequestMapping("${api.base-path}")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentResource {
    String basePath;
    CommentService commentService;

    public CommentResource(@Value("${api.base-path}") String basePath, CommentService commentService) {
        this.basePath = basePath;
        this.commentService = commentService;
    }

    @GetMapping(Routes.Video.basePath + "/{videoId}" + Routes.comments)
    public Mono<ResponseEntity<BaseResponse<PagingResponse<CommentDTO>>>> getAll(@PathVariable(value = "videoId") String videoId, PagingRequest pagingRequest) {
        return commentService.getAll(videoId, pagingRequest).map(ResponseEntity::ok);
    }

    @GetMapping(  Routes.comments + "/{id}")
    public ResponseEntity<Mono<BaseResponse<CommentDTO>>> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(commentService.get(id));
    }

    @PostMapping(  Routes.comments)
    public Mono<ResponseEntity<BaseResponse<CommentDTO>>> create(@RequestBody @Valid CreateCommentDTO dto) {
        return commentService.create(dto)
                .map((res) -> {
                    long id = res.getData().getId();

                    return ResponseEntity.created(URI.create(STR."\{basePath}/\{Routes.comments}/\{id}")).body(res);
                });
    }

    @PatchMapping(  Routes.comments + "/{id}")
    public Mono<ResponseEntity<BaseResponse<CommentDTO>>> update(
            @PathVariable("id") long id,
            @RequestBody @Valid CommentResource.UpdateCommentDTO dto
    ) {
        return commentService.update(id, dto.getContent()).map(ResponseEntity::ok);
    }

    @DeleteMapping(  Routes.comments + "/{id}")
    public Mono<ResponseEntity<BaseResponse<Void>>> delete(@PathVariable("id") long id) {
        return commentService.delete(id).map(ResponseEntity.ok()::body);
    }

    @Getter
    public static class UpdateCommentDTO {
        @NotBlank
        String content;
    }
}
