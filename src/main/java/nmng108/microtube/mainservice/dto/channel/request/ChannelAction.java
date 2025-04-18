package nmng108.microtube.mainservice.dto.channel.request;

import org.springframework.lang.NonNull;

public enum ChannelAction {
    SUBSCRIBE,
    UNSUBSCRIBE;

    public static ChannelAction from(@NonNull String name) {
        for (ChannelAction value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }
}
