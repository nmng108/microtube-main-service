package nmng108.microtube.mainservice.dto.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.User;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserDTO {
    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-z]+[a-z\\d]*(_[a-z\\d]+)*$")
    String username;
    @NotNull
    @Size(min = 3, max = 50)
    String name;
    @NotNull
    @Size(max = 100)
    @Pattern(regexp = "^\\w+(\\.\\w+)*@\\w+(\\.\\w+)+$")
    String email;
    @Size(min = 15, max = 20)
    @Pattern(regexp = "^\\+\\d{1,3} \\d+")
    String phoneNumber;

    public User toUser() {
        return User.builder()
                .username(username)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }
}
