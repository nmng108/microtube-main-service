package nmng108.microtube.mainservice.util.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import nmng108.microtube.mainservice.dto.video.request.VideoUpdateType;
import org.springframework.lang.Nullable;

import java.io.IOException;

public final class JsonConverters {
    public static final class VideoUpdateTypeDeserializer extends JsonDeserializer<VideoUpdateType> {
        @Override
        @Nullable
        public VideoUpdateType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            return switch (currentToken) {
                case JsonToken.VALUE_NUMBER_INT -> VideoUpdateType.from(jsonParser.getIntValue());
                case JsonToken.VALUE_STRING -> VideoUpdateType.from(jsonParser.getText());
                default -> null;
            };
        }
    }
}
