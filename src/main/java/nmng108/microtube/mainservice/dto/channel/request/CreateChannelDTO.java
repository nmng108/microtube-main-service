package nmng108.microtube.mainservice.dto.channel.request;

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
    @NotNull
    @Min(0)
    long userId;
    @Size(min = 3, max = 15)
    String name;
    @Pattern(regexp = "^[a-z]+[a-z\\d]*(_[a-z\\d]+)*$")
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
