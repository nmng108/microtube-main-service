package nmng108.microtube.mainservice.repository.projection;

import lombok.Getter;
import nmng108.microtube.mainservice.entity.Channel;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class ChannelWithPersonalSubscription extends Channel {
    private Integer subscribed;
}
