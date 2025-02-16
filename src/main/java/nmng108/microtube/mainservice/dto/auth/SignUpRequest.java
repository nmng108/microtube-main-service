package nmng108.microtube.mainservice.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class SignUpRequest {
    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(regexp = "^\\w+$")
    String username;
    @NotNull
    @Size(min = 6, max = 50)
    String password;
    @Email
    String email;

//    public User toUser() {
//        return User.builder()
//                .username(username)
//                .password(password)
//                .email(email)
//                .build();
//    }
}
