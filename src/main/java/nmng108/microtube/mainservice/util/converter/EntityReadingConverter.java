package nmng108.microtube.mainservice.util.converter;

import io.r2dbc.spi.Row;
import nmng108.microtube.mainservice.entity.Playlist;
import nmng108.microtube.mainservice.entity.Video;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class EntityReadingConverter {
    public static final List<Converter<?, ?>> getAllConverters = List.of(
            new EntityReadingConverter.VideoConverter(),
            new EntityReadingConverter.PlaylistConverter()
    );

    @ReadingConverter
    public static final class VideoConverter implements Converter<Row, Video> {
        @Override
        public Video convert(Row source) {
            Video video = new Video();

            video.setId(Objects.requireNonNullElse(source.get("ID", Long.class), 0).longValue());
            video.setCode(source.get("CODE", String.class));
            video.setTitle(source.get("TITLE", String.class));
            video.setDescription(source.get("DESCRIPTION", String.class));
            video.setVisibility(VideoVisibilityConverters.PersistenceConverter.getInstance().convertToEntityAttribute(Objects.requireNonNull(source.get("VISIBILITY", Integer.class))));
            video.setThumbnail(source.get("THUMBNAIL", String.class));
            video.setOriginalFilename(source.get("ORIGINAL_FILENAME", String.class));
            video.setTempFilepath(source.get("TEMP_FILEPATH", String.class));
            video.setDestFilepath(source.get("DEST_FILEPATH", String.class));
            video.setStatus(VideoStatusConverters.PersistenceConverter.getInstance().convertToEntityAttribute(Objects.requireNonNull(source.get("STATUS", Integer.class))));
            video.setViewCount(Objects.requireNonNullElse(source.get("VIEW_COUNT", Long.class), 0L));
            video.setLikeCount(Objects.requireNonNullElse(source.get("LIKE_COUNT", Long.class), 0L));
            video.setDislikeCount(Objects.requireNonNullElse(source.get("DISLIKE_COUNT", Long.class), 0L));
            video.setAllowsComment(Objects.requireNonNullElse(source.get("ALLOW_COMMENT", Integer.class), 0) == 1);
            video.setChannelId(Objects.requireNonNullElse(source.get("CHANNEL_ID", Long.class), 0).longValue());
            video.setCreatedBy(source.get("CREATED_BY", Long.class));
            video.setCreatedAt(source.get("CREATED_AT", LocalDateTime.class));
            video.setModifiedBy(source.get("MODIFIED_BY", Long.class));
            video.setModifiedAt(source.get("MODIFIED_AT", LocalDateTime.class));

            return video;
        }
    }

    @ReadingConverter
    public static final class PlaylistConverter implements Converter<Row, Playlist> {
        @Override
        public Playlist convert(Row source) {
            Playlist playlist = new Playlist();

            playlist.setId(Objects.requireNonNullElse(source.get("ID", Long.class), 0).longValue());
            playlist.setCode(source.get("CODE", String.class));
            playlist.setName(source.get("NAME", String.class));
            playlist.setDescription(source.get("DESCRIPTION", String.class));
            playlist.setVisibility(PlaylistVisibilityConverters.PersistenceConverter.getInstance().convertToEntityAttribute(Objects.requireNonNull(source.get("VISIBILITY", Integer.class))));
            playlist.setUserId(Optional.ofNullable(source.get("USER_ID", Long.class)).orElse(null));
            playlist.setCreatedBy(source.get("CREATED_BY", Long.class));
            playlist.setCreatedAt(source.get("CREATED_AT", LocalDateTime.class));
            playlist.setModifiedBy(source.get("MODIFIED_BY", Long.class));
            playlist.setModifiedAt(source.get("MODIFIED_AT", LocalDateTime.class));

            return playlist;
        }
    }
}
