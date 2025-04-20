package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(name = "CHANNEL", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Channel extends Accountable {
    @Id
    long id;
    @Column("NAME")
    String name;
    @Column("PATHNAME")
    String pathname;
    @Column("DESCRIPTION")
    String description;
    @Column("AVATAR")
    String avatar;
    @Column("SUBSCRIPTION_COUNT")
    long subscriptionCount;
    @Column("VIDEO_COUNT")
    long videoCount;

    @Column("USER_ID")
    long userId;

//    public Channel(Channel other) {
//        super(other);
//        this.id = other.id;
//        this.name = other.name;
//        this.pathname = other.pathname;
//        this.description = other.description;
//        this.avatar = other.avatar;
//        this.subscriptionCount = other.subscriptionCount;
//        this.videoCount = other.videoCount;
//        this.userId = other.userId;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel o1 = (Channel) o;
        return id != 0 && o1.id != 0 && id == o1.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
