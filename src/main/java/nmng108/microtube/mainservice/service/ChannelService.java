package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.channel.request.CreateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.request.UpdateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.response.ChannelDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChannelService {
    Mono<BaseResponse<List<ChannelDTO>>> getAllChannels();

    Mono<BaseResponse<ChannelDTO>> getChannelByIdOrPathName(String id);

    Mono<BaseResponse<ChannelDTO>> createChannelInfo(CreateChannelDTO dto);

    Mono<BaseResponse<ChannelDTO>> updateChannelInfo(String identifiable, UpdateChannelDTO dto);

    Mono<BaseResponse<Void>> deleteChannel(String identifiable);
}