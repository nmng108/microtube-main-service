package nmng108.microtube.mainservice.router;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingRequest;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.channel.request.ChannelAction;
import nmng108.microtube.mainservice.dto.channel.request.CreateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.request.UpdateChannelDTO;
import nmng108.microtube.mainservice.dto.channel.response.ChannelDTO;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.service.ChannelService;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;


@RestController
@RequestMapping("${api.base-path}" + Routes.Channel.basePath)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChannelResource {
    String basePath;
    ChannelService channelService;

    public ChannelResource(@Value("${api.base-path}") String basePath, ChannelService channelService) {
        this.basePath = basePath + Routes.Channel.basePath;
        this.channelService = channelService;
    }

    @GetMapping
    public ResponseEntity<Mono<BaseResponse<PagingResponse<ChannelDTO>>>> getAll(PagingRequest pagingRequest, @RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(channelService.getAll(pagingRequest, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<ChannelDTO>>> getByIdOrPathname(@PathVariable("id") String id) {
        return ResponseEntity.ok(channelService.getByIdOrPathName(id));
    }

    @PostMapping
    public Mono<ResponseEntity<BaseResponse<ChannelDTO>>> create(@RequestBody @Valid CreateChannelDTO dto) {
        return channelService.create(dto)
                .map((res) -> {
                    long id = res.getData().getId();

                    return ResponseEntity.created(URI.create(STR."\{basePath}/\{id}")).body(res);
                });
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<BaseResponse<ChannelDTO>>> update(@PathVariable("id") String id, @RequestBody @Valid UpdateChannelDTO dto) {
        return channelService.update(id, dto).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<BaseResponse<Void>>> delete(@PathVariable("id") String id) {
        return channelService.delete(id).map(ResponseEntity.accepted()::body);
    }

    @PatchMapping("/{id}/{action}")
    public Mono<ResponseEntity<BaseResponse<Void>>> doAction(@PathVariable("id") String id, @PathVariable("action") String actionName) {
        ChannelAction channelAction = ChannelAction.from(actionName);

        if (channelAction == null) {
            throw new BadRequestException("Invalid action");
        }

        return channelService.doAction(id, channelAction).map(ResponseEntity::ok);
    }
}
