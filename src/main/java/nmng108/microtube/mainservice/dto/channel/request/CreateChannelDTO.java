package nmng108.microtube.mainservice.dto.channel.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Channel;

import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateChannelDTO {
    @NotBlank
    @Size(min = 3, max = 25)
    String name;
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-z]+[a-z\\d]*([_-][a-z\\d]+)*$")
    @Schema(description = "Customize pathname to the channel. By default it will also be username.")
    String pathname;
    @Size(max = 2000)
    String description;

    public Channel toChannel() {
        return Channel.builder()
                .name(name.strip())
                .pathname(Optional.ofNullable(pathname).map(String::strip).orElse(null))
                .description(Optional.ofNullable(description).map(String::strip).orElse(null))
                .build();
    }
}
