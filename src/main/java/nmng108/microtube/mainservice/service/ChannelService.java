package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.channel.request.ChannelAction;
import nmng108.microtube.mainservice.dto.channel.request.CreateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.request.UpdateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.response.ChannelDTO;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

public interface ChannelService {
    Mono<BaseResponse<PagingResponse<ChannelDTO>>> getAll(PagingRequest pagingRequest, @Nullable String name);

    Mono<BaseResponse<ChannelDTO>> getByIdOrPathName(String id);

    //    @PreAuthorize("hasAnyAuthority('CHANNEL_ALL', 'CHANNEL_CREATE')")
    Mono<BaseResponse<ChannelDTO>> create(CreateChannelDTO dto);

    //    @PreAuthorize("hasAnyAuthority('CHANNEL_ALL', 'CHANNEL_UPDATE')")
    Mono<BaseResponse<ChannelDTO>> update(String identifiable, UpdateChannelDTO dto);

    //    @PreAuthorize("hasAnyAuthority('CHANNEL_ALL', 'CHANNEL_DELETE')")
    Mono<BaseResponse<Void>> delete(String identifiable);

    Mono<BaseResponse<Void>> doAction(String identifiable, ChannelAction action);
}