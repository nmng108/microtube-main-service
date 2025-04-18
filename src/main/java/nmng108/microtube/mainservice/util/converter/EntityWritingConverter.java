package nmng108.microtube.mainservice.util.converter;

import nmng108.microtube.mainservice.entity.Video;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

import java.time.LocalDateTime;
import java.util.List;

public final class EntityWritingConverter {
    public static final List<Converter<?, ?>> getAllConverters = List.of(
            new EntityWritingConverter.VideoConverter()
    );

    @WritingConverter
    public static final class VideoConverter implements Converter<Video, OutboundRow> {
        @Override
        public OutboundRow convert(Video source) {
            OutboundRow row = new OutboundRow();

            // Map each field in the Video entity to the corresponding column in the database
            row.put("ID", Parameter.from(source.getId()));
            row.put("CODE", Parameter.from(source.getCode()));
            row.put("TITLE", Parameter.from(source.getTitle()));
            row.put("DESCRIPTION", Parameter.fromOrEmpty(source.getDescription(), String.class));
            row.put("VISIBILITY", Parameter.from(source.getVisibility().getPersistedValue())); // Convert enum to int
            row.put("STATUS", Parameter.from(source.getStatus().getPersistedValue())); // Convert enum to int
            row.put("ALLOW_COMMENT", Parameter.from(source.isAllowsComment()));
            row.put("CHANNEL_ID", Parameter.from(source.getChannelId()));
            row.put("CREATED_BY", Parameter.from(source.getCreatedBy()));
            row.put("CREATED_AT", Parameter.from(source.getCreatedAt()));
            row.put("MODIFIED_BY", Parameter.fromOrEmpty(source.getModifiedBy(), Long.class));
            row.put("MODIFIED_AT", Parameter.fromOrEmpty(source.getModifiedAt(), LocalDateTime.class));

            return row;
        }
    }
}
