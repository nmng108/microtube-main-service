package nmng108.microtube.mainservice.dto.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class GrantedAuthorityDTO implements GrantedAuthority {
    Integer id;
    String authority;

    public GrantedAuthorityDTO(int id, String code) {
        this.id = id;
        this.authority = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrantedAuthorityDTO user = (GrantedAuthorityDTO) o;
        return id != 0 && user.id != 0 && id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
