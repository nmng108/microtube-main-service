package nmng108.microtube.mainservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Accountable {
    @Column("CREATED_BY")
    @CreatedBy
    Long createdBy;
    @Column("CREATED_AT")
    @CreatedDate
    LocalDateTime createdAt;
    @Column("MODIFIED_BY")
    @LastModifiedBy
    Long modifiedBy;
    @Column("MODIFIED_AT")
    @LastModifiedDate
    LocalDateTime modifiedAt;
    @Column("DELETED_BY")
    private Long deletedBy;
    @Column("DELETED_AT")
    private LocalDateTime deletedAt;

    public Accountable(Accountable other) {
        this.createdBy = other.createdBy;
        this.createdAt = other.createdAt;
        this.modifiedBy = other.modifiedBy;
        this.modifiedAt = other.modifiedAt;
        this.deletedBy = other.deletedBy;
        this.deletedAt = other.deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public static class AttributeName {
        public static final String createdBy = "createdBy";
        public static final String modifiedBy = "modifiedBy";
        public static final String createdAt = "createdAt";
        public static final String modifiedAt = "modifiedAt";
    }
}
