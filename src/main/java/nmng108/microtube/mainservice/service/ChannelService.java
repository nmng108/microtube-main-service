package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.channel.request.CreateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.request.UpdateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.response.ChannelDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChannelService {
    Mono<BaseResponse<List<ChannelDTO>>> getAllChannels(PagingRequest pagingRequest);

    Mono<BaseResponse<ChannelDTO>> getChannelByIdOrPathName(String id);

    @PreAuthorize("hasAnyAuthority('CHANNEL_ALL', 'CHANNEL_CREATE')")
    Mono<BaseResponse<ChannelDTO>> createChannelInfo(CreateChannelDTO dto);

    @PreAuthorize("hasAnyAuthority('CHANNEL_ALL', 'CHANNEL_UPDATE')")
    Mono<BaseResponse<ChannelDTO>> updateChannelInfo(String identifiable, UpdateChannelDTO dto);

    @PreAuthorize("hasAnyAuthority('CHANNEL_ALL', 'CHANNEL_DELETE')")
    Mono<BaseResponse<Void>> deleteChannel(String identifiable);
}