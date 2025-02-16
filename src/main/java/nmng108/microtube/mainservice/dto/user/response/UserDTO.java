package nmng108.microtube.mainservice.dto.user.response;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.User;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    long id;
    String username;
    String name;
    String email;
    String phoneNumber;
    long createdBy;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.createdAt = user.getCreatedAt();
        this.createdBy = user.getCreatedBy();
        this.modifiedAt = user.getModifiedAt();
    }
}
