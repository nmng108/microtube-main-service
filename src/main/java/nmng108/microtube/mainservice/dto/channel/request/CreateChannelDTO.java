package nmng108.microtube.mainservice.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Channel;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateChannelDTO {
//    @NotNull
    @Min(0)
    @Schema(description = "Dev plan: remove this prop due to an user can only create a channel for his/her own")
    long userId;
    @Size(min = 3, max = 15)
    String name;
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-z]+[a-z\\d]*(_[a-z\\d]+)*$")
    @Schema(description = "Customize pathname to the channel. By default it will also be username.")
    String pathName;
    @Size(max = 2000)
    String description;

    public Channel toChannel() {
        return Channel.builder()
                .userId(userId)
                .name(name)
                .pathName(pathName)
                .description(description)
                .build();
    }
}
