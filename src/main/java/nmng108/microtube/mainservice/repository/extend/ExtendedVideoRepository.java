package nmng108.microtube.mainservice.repository.extend;

import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
import nmng108.microtube.mainservice.util.converter.EntityReadingConverter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface ExtendedVideoRepository {
    VideoQuery buildQuery(SearchVideoDTO criteria);

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Slf4j
    final class VideoQuery implements QueryDefinition<VideoWithChannelOwner> {
        DatabaseClient databaseClient;
        String queryBuilder;
        Integer queryingPageNumber;
        Integer size;
        Map<String, Object> paramBindings;
        List<String> orderedFields;

        public VideoQuery(DatabaseClient databaseClient, SearchVideoDTO criteria) {
            this.databaseClient = databaseClient;
            this.queryingPageNumber = criteria.getQueryingPageNumber();
            this.size = criteria.getSize();
            this.paramBindings = new LinkedHashMap<>();
            this.orderedFields = new LinkedList<>();

            String accessingUserIdParam = "accessingUserId";
            StringBuilder queryBuilder = new StringBuilder(STR."""
                    SELECT v.*, c.PATHNAME channelPathname, c.NAME channelName, c.AVATAR channelAvatar, c.USER_ID userId,
                        urv.PAUSE_POSITION pausePosition, urv.REACTION reaction,
                        (SELECT COUNT(0) FROM COMMENT WHERE VIDEO_ID = v.ID) commentCount
                    FROM VIDEO v
                        JOIN CHANNEL c ON v.CHANNEL_ID = c.ID
                        LEFT JOIN USER_RELATION_VIDEO urv ON v.ID = urv.VIDEO_ID AND urv.USER_ID = :\{accessingUserIdParam}
                    """);
            List<String> predicates = new LinkedList<>();

            // Utilize Parameters.in to pass nullable value to a param
            paramBindings.put(accessingUserIdParam, Parameters.in(R2dbcType.DOUBLE, criteria.getAccessingUserId()));

            if (criteria.getSubscribed() != null) {
                if (criteria.getAccessingUserId() != null) {
                    queryBuilder.append(STR." LEFT JOIN CHANNEL_SUBSCRIPTION cs ON c.ID = cs.CHANNEL_ID AND cs.USER_ID = :\{accessingUserIdParam}");
                    predicates.add(STR."cs.CHANNEL_ID IS \{criteria.getSubscribed() ? "NOT" : ""} NULL");
                } else {
                    predicates.add("1 = 0");
                }
            }

            String allowedVisibilityParam = "allowedVisibility";

            // Check nullity & set again just for safety
            if (CollectionUtils.isEmpty(criteria.getAllowedVisibility())) {
                criteria.setAllowedVisibility(List.of(Video.Visibility.PUBLIC.number));
            }

            if (criteria.getAccessingUserId() != null) {
                // TODO: may divide into 2 subqueries to avoid using OR (decision depends on actual performance).
                //  Second benefit: Recommended list should not show owned videos, so using only the first subquery fits that need.
                predicates.add(STR."((c.USER_ID != :\{accessingUserIdParam} AND v.VISIBILITY IN (:\{allowedVisibilityParam})) OR c.USER_ID = :\{accessingUserIdParam})");
            } else {
                predicates.add(STR."v.VISIBILITY IN (:\{allowedVisibilityParam})");
            }

            paramBindings.put(allowedVisibilityParam, criteria.getAllowedVisibility()/*.stream().map(String::valueOf).collect(Collectors.joining(",", "(", ")"))*/);

            if (criteria.getReaction() != null) {
                String param = "reaction";

                predicates.add(STR."urv.REACTION = :\{param}");
                paramBindings.put(param, criteria.getReaction());
            }

            if (criteria.getChannelId() != null) {
                String param = "channelId";

                predicates.add(STR."v.CHANNEL_ID = :\{param}");
                paramBindings.put(param, criteria.getChannelId());
            }

            if (!CollectionUtils.isEmpty(criteria.getIds())) {
                String param = "ids";

                predicates.add(STR."v.ID in (:\{param})");
                paramBindings.put(param, criteria.getIds());
            }

            if (StringUtils.hasText(criteria.getName())) {
                String param = "name";

                predicates.add(STR."(MATCH(v.TITLE, v.DESCRIPTION) AGAINST(:\{param}) OR MATCH(c.PATHNAME, c.NAME, c.DESCRIPTION) AGAINST(:\{param}))");
                paramBindings.put(param, criteria.getName());
                orderedFields.add(STR."MATCH(v.TITLE, v.DESCRIPTION) AGAINST(:\{param}) DESC, MATCH(c.PATHNAME, c.NAME, c.DESCRIPTION) AGAINST(:\{param}) DESC");
            }

            if (!predicates.isEmpty()) {
                queryBuilder.append("\nWHERE ").append(String.join(" AND ", predicates));
            }

            this.queryBuilder = queryBuilder.toString();
            orderedFields.addAll(List.of("v.CREATED_AT DESC", "v.ID DESC"));
        }

        @Override
        public Flux<VideoWithChannelOwner> find() {
            var converter = new EntityReadingConverter.VideoConverter();

            return databaseClient.sql(STR."\{queryBuilder} ORDER BY \{String.join(",", orderedFields)} LIMIT :size OFFSET :offset")
                    .bind("size", size)
                    .bind("offset", size * queryingPageNumber)
                    .bindValues(paramBindings)
                    .map((row, rowMetadata) -> {
                        Video video = converter.convert(row);

                        return new VideoWithChannelOwner(
                                video,
                                row.get("channelPathname", String.class),
                                row.get("channelName", String.class),
                                row.get("channelAvatar", String.class),
                                row.get("userId", Long.class),
                                row.get("pausePosition", Long.class),
                                row.get("reaction", Integer.class),
                                row.get("commentCount", Long.class)
                        );
                    })
                    .all();
        }

        @Override
        public Mono<Long> countTotal() {
            String finalQuery = STR."SELECT COUNT(0) FROM (\{queryBuilder}) t";

            return databaseClient.sql(finalQuery).bindValues(paramBindings).mapValue(Long.class).one();
        }
    }
}
