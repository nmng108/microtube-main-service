package nmng108.microtube.mainservice.util.converter;

import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import org.springframework.core.convert.converter.Converter;

public final class WebParamConverters {
    public static final class SearchVideoDTOSortedFieldConverter implements Converter<String, SearchVideoDTO.SortedField> {
        @Override
        public SearchVideoDTO.SortedField convert(String source) {
            return SearchVideoDTO.SortedField.from(source);
        }
    }
}
