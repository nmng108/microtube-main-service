package nmng108.microtube.mainservice.util.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import nmng108.microtube.mainservice.entity.Video;
import org.springframework.lang.Nullable;

import java.io.IOException;

public final class VideoVisibilityConverters {
//    public static class IntegerToVisibilityConverter implements Converter<Integer, Video.Visibility> {
//        @Override
//        public Video.Visibility convert(Integer source) {
//            return Video.Visibility.valueOf(source);
//        }
//    }
//
//    public static class VisibilityToIntegerConverter implements Converter<Video.Visibility, Integer> {
//        @Override
//        public Integer convert(Video.Visibility source) {
//            return source.number;
//        }
//    }

    public static final class PersistenceConverter extends AbstractPersistentEnumConverter<Video.Visibility, Integer> {
        private static final PersistenceConverter INSTANCE = new PersistenceConverter();

        private PersistenceConverter() {
            super(Video.Visibility.class);
        }

        public static AbstractPersistentEnumConverter<Video.Visibility, Integer> getInstance() {
            return INSTANCE;
        }
    }

    public static class Deserializer extends JsonDeserializer<Video.Visibility> {
        @Override
        @Nullable
        public Video.Visibility deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            return switch (currentToken) {
                case JsonToken.VALUE_NUMBER_INT -> Video.Visibility.valueOf(jsonParser.getIntValue());
                case JsonToken.VALUE_STRING -> Video.Visibility.valueOf(jsonParser.getText());
                default -> null;
            };
        }
    }

//    public static class VisibilitySerializer extends JsonSerializer<Video.Visibility> {
//        @Override
//        public void serialize(Video.Visibility visibility, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//            jsonGenerator.writeNumber(visibility.number);
//        }
//    }
}
