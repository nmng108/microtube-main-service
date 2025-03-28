package nmng108.microtube.mainservice.dto.channel.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateChannelDTO {
    @Size(min = 3, max = 25)
    String name;
    @Size(max = 2000)
    String description;
    Integer visibility;
}
