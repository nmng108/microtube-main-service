package nmng108.microtube.mainservice.dto.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UpdateUserDTO {
    @Size(min = 3, max = 50)
    String name;
    @Size(max = 100)
    @Pattern(regexp = "^\\w+(\\.\\w+)*@\\w+(\\.\\w+)+$")
    String email;
    @Size(min = 10, max = 15)
    String phoneNumber;
}
