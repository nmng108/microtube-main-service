package nmng108.microtube.mainservice.repository.extend.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import nmng108.microtube.mainservice.repository.extend.ExtendedVideoRepository;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExtendedVideoRepositoryImpl implements ExtendedVideoRepository {
    DatabaseClient databaseClient;

    public ExtendedVideoRepositoryImpl(@Qualifier(Constants.BeanName.Database.MainRelationalDatabase.DATABASE_CLIENT) DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public VideoQuery buildQuery(SearchVideoDTO dto) {
        return new VideoQuery(databaseClient, dto);
    }
}
