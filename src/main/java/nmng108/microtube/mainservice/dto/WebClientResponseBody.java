package nmng108.microtube.mainservice.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class WebClientResponseBody<T> {
    Integer status;
    String message;
    T data;
}
