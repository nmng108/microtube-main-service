package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "VIDEO", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Video extends Accountable {
    @Id
    long id; // TODO: may use Base64 64-bit string instead
    @Column("NAME")
    String name;
    @Column("DESCRIPTION")
    String description;
    @Column("VISIBILITY")
    int visibility;

    @Column("CHANNEL_ID")
    long channelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video o1 = (Video) o;
        return id != 0 && o1.id != 0 && id == o1.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    @Getter
    public enum Visibility {
        PRIVATE(1, "Private"),
        NOT_LISTED(2, "Not listed"),
        PUBLIC(3, "Public");

        int number;
        String name;

        Visibility(int number, String name) {
            this.number = number;
            this.name = name;
        }
    }
}
