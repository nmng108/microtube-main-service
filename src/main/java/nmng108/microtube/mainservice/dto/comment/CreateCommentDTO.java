package nmng108.microtube.mainservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Comment;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateCommentDTO {
    @NotBlank
    String videoId;
    @NotBlank
    String content;
    Long parentId;
    @Min(1)
    int level;

    public Comment toComment() {
        return Comment.builder()
                .content(content)
                .parentId(parentId)
                .level(level)
                .build();
    }
}
