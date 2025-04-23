package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.comment.CommentDTO;
import nmng108.microtube.mainservice.dto.comment.CreateCommentDTO;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

public interface CommentService {
    Mono<BaseResponse<PagingResponse<CommentDTO>>> getAll(String videoId, @Nullable Integer parentId, PagingRequest pagingRequest);

    Mono<BaseResponse<CommentDTO>> get(long id);

    Mono<BaseResponse<CommentDTO>> create(CreateCommentDTO dto);

    Mono<BaseResponse<CommentDTO>> update(long id, String content);

    Mono<BaseResponse<Void>> delete(long id);
}