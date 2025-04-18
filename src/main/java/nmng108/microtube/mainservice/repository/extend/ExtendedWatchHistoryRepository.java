//package nmng108.microtube.mainservice.repository.extend;
//
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
//import nmng108.microtube.mainservice.entity.Video;
//import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
//import nmng108.microtube.mainservice.util.converter.EntityReadingConverter;
//import org.springframework.r2dbc.core.DatabaseClient;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//public interface ExtendedWatchHistoryRepository {
//    WatchHistoryQuery buildQuery(SearchVideoDTO criteria);
//
//    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//    @Slf4j
//    final class WatchHistoryQuery implements QueryDefinition<VideoWithChannelOwner> {
//        DatabaseClient databaseClient;
//        String queryBuilder;
//        Integer queryingPageNumber;
//        Integer size;
//        Map<String, Object> paramBindings;
//        List<String> orderedFields;
//
//        public WatchHistoryQuery(DatabaseClient databaseClient, SearchVideoDTO criteria) {
//            this.databaseClient = databaseClient;
//            this.queryingPageNumber = criteria.getQueryingPageNumber();
//            this.size = criteria.getSize();
//            this.paramBindings = new LinkedHashMap<>();
//            this.orderedFields = new LinkedList<>();
//
//            StringBuilder queryBuilder = new StringBuilder("""
//                    SELECT v.*, c.PATHNAME channelPathname, c.NAME channelName, c.AVATAR channelAvatar, c.USER_ID userId
//                    FROM WATCH_HISTORY w
//                        JOIN VIDEO v ON w.VIDEO_ID = v.ID
//                        JOIN CHANNEL c ON v.CHANNEL_ID = c.ID
//                    """);
//            List<String> predicates = new LinkedList<>();
//
//            if (criteria.getChannelId() != null) {
//                String param = "channelId";
//
//                predicates.add(STR."v.CHANNEL_ID = :\{param}");
//                paramBindings.put(param, criteria.getChannelId());
//            }
//
//            if (!CollectionUtils.isEmpty(criteria.getIds())) {
//                String param = "ids";
//
//                predicates.add(STR."v.ID in (:\{param})");
//                paramBindings.put(param, criteria.getIds());
//            }
//
//            if (StringUtils.hasText(criteria.getName())) {
//                String param = "name";
//
//                predicates.add(STR."MATCH(v.NAME, v.DESCRIPTION) AGAINST(:\{param})");
//                paramBindings.put(param, criteria.getName());
//                orderedFields.add(STR."MATCH(v.NAME, v.DESCRIPTION) AGAINST(:\{param}) DESC");
//            }
//
//            if (!predicates.isEmpty()) {
//                queryBuilder.append("\nWHERE ").append(String.join(",", predicates));
//            }
//
//            this.queryBuilder = queryBuilder.toString();
//            orderedFields.addAll(List.of("v.CREATED_AT DESC", "v.ID DESC"));
//        }
//
//        @Override
//        public Flux<VideoWithChannelOwner> find() {
//            var converter = new EntityReadingConverter.VideoConverter();
//
//            return databaseClient.sql(STR."\{queryBuilder} ORDER BY \{String.join(",", orderedFields)} LIMIT :size OFFSET :offset")
//                    .bind("size", size)
//                    .bind("offset", size * queryingPageNumber)
//                    .bindValues(paramBindings)
//                    .map((row, rowMetadata) -> {
//                        Video video = converter.convert(row);
//
//                        return new VideoWithChannelOwner(
//                                video,
//                                row.get("channelPathname", String.class),
//                                row.get("channelName", String.class),
//                                row.get("channelAvatar", String.class),
//                                row.get("userId", Long.class)
//                        );
//                    })
//                    .all();
//        }
//
//        @Override
//        public Mono<Long> countTotal() {
//            String finalQuery = STR."SELECT COUNT(0) FROM (\{queryBuilder}) t";
//
//            return databaseClient.sql(finalQuery).bindValues(paramBindings).mapValue(Long.class).one();
//        }
//    }
//}
