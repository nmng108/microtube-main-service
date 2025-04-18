package nmng108.microtube.mainservice.dto.video.request;

import org.springframework.lang.Nullable;

public enum VideoUpdateType {
    UPDATE_INFO,
    LIKE,
    DISLIKE,
    CANCEL_LIKE,
    CANCEL_DISLIKE,
    INCREASE_VIEW,
    ;

    @Nullable
    public static VideoUpdateType from(int number) {
        for (VideoUpdateType value : values()) {
            if (value.ordinal() == number) {
                return value;
            }
        }

        return null;
    }

    @Nullable
    public static VideoUpdateType from(String name) {
        for (VideoUpdateType value : values()) {
            if (value.toString().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }
}
