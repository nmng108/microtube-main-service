package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import nmng108.microtube.mainservice.util.constant.Constants;
import nmng108.microtube.mainservice.util.converter.PersistentEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Table(name = "PLAYLIST", schema = Constants.DATABASE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Playlist extends Accountable implements Cloneable {
    @Id
    @Column("ID")
    long id;
    @Column("CODE")
    String code;
    @Column("NAME")
    String name;
    @Column("DESCRIPTION")
    String description;
    @Column("VISIBILITY")
    Visibility visibility;
    @Column("USER_ID")
    long userId;

    public Playlist(Playlist other) {
        super(other);
        this.id = other.id;
        this.code = other.code;
        this.name = other.name;
        this.description = other.description;
        this.visibility = other.visibility;
        this.userId = other.userId;
    }

    //    public Visibility getVisibility() {
//        return VideoVisibilityConverters.PersistenceConverter.getInstance().convertToEntityAttribute(visibility);
//    }

//    public void setVisibility(Visibility visibility) {
//        this.visibility = visibility.number;
//    }
//
//    public Status getStatus() {
//        return Arrays.stream(Status.values()).filter((s) -> s.number == status).findFirst().orElseThrow(
//                () -> new RuntimeException("No status found for " + status)
//        );
//    }

//    public void setStatus(Status status) {
//        this.status = status.number;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist o1 = (Playlist) o;
        return id != 0 && o1.id != 0 && id == o1.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Playlist(this);
    }

    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    @Getter
    public enum Visibility implements PersistentEnum<Integer> {
        PRIVATE(1, "PRIVATE", "Private"),
        NOT_LISTED(2, "NOT_LISTED", "Not listed"),
        PUBLIC(3, "PUBLIC", "Public");

        int number;
        String code;
        String name;

        Visibility(int number, String code, String name) {
            this.number = number;
            this.code = code;
            this.name = name;
        }

        @Nullable
        public static Visibility valueOf(int number) {
            return Arrays.stream(values()).filter((v) -> v.number == number).findFirst().orElse(null);
        }

        @Override
        public Integer getPersistedValue() {
            return number;
        }
    }
}
