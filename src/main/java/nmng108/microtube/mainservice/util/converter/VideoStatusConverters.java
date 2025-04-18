package nmng108.microtube.mainservice.util.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import nmng108.microtube.mainservice.entity.Video;
import org.springframework.lang.Nullable;

import java.io.IOException;

public final class VideoStatusConverters {
//    public static class IntegerToVisibilityConverter implements Converter<Integer, Video.Status> {
//        @Override
//        public Video.Status convert(Integer source) {
//            return Video.Status.valueOf(source);
//        }
//    }
//
//    public static class VisibilityToIntegerConverter implements Converter<Video.Status, Integer> {
//        @Override
//        public Integer convert(Video.Status source) {
//            return source.number;
//        }
//    }

    public static final class PersistenceConverter extends AbstractPersistentEnumConverter<Video.Status, Integer> {
        private static final PersistenceConverter INSTANCE = new PersistenceConverter();

        private PersistenceConverter() {
            super(Video.Status.class);
        }

        public static AbstractPersistentEnumConverter<Video.Status, Integer> getInstance() {
            return INSTANCE;
        }
    }

    public static class Deserializer extends JsonDeserializer<Video.Status> {
        @Override
        @Nullable
        public Video.Status deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            return switch (currentToken) {
                case JsonToken.VALUE_NUMBER_INT -> Video.Status.valueOf(jsonParser.getIntValue());
                case JsonToken.VALUE_STRING -> Video.Status.valueOf(jsonParser.getText());
                default -> null;
            };
        }
    }

//    public static class VisibilitySerializer extends JsonSerializer<Video.Status> {
//        @Override
//        public void serialize(Video.Status visibility, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//            jsonGenerator.writeNumber(visibility.number);
//        }
//    }
}
