package nmng108.microtube.mainservice.util.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import nmng108.microtube.mainservice.entity.Playlist;
import org.springframework.lang.Nullable;

import java.io.IOException;

public final class PlaylistVisibilityConverters {
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

    public static final class PersistenceConverter extends AbstractPersistentEnumConverter<Playlist.Visibility, Integer> {
        private static final PersistenceConverter INSTANCE = new PersistenceConverter();

        private PersistenceConverter() {
            super(Playlist.Visibility.class);
        }

        public static AbstractPersistentEnumConverter<Playlist.Visibility, Integer> getInstance() {
            return INSTANCE;
        }
    }

    public static class Deserializer extends JsonDeserializer<Playlist.Visibility> {
        @Override
        @Nullable
        public Playlist.Visibility deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            return switch (currentToken) {
                case JsonToken.VALUE_NUMBER_INT -> Playlist.Visibility.valueOf(jsonParser.getIntValue());
                case JsonToken.VALUE_STRING -> Playlist.Visibility.valueOf(jsonParser.getText());
                default -> null;
            };
        }
    }

//    public static class VisibilitySerializer extends JsonSerializer<Playlist.Visibility> {
//        @Override
//        public void serialize(Playlist.Visibility visibility, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//            jsonGenerator.writeNumber(visibility.number);
//        }
//    }
}
