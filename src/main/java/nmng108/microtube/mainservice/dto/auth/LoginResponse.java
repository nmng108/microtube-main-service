package nmng108.microtube.mainservice.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LoginResponse {
    String username;
    Date expireAt;
    String token;
}
