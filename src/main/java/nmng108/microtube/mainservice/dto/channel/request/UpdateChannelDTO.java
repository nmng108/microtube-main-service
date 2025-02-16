package nmng108.microtube.mainservice.dto.channel.request;

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
    String name;
    String description;
    Integer visibility;
}
