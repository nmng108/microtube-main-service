package nmng108.microtube.mainservice.configuration.database;

import io.r2dbc.spi.ConnectionFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.util.constant.Constants;
import nmng108.microtube.mainservice.util.converter.EntityReadingConverter;
import nmng108.microtube.mainservice.util.converter.EntityWritingConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Replicate this class file & rename beans to create connection to another database
 */
@Configuration
@EnableR2dbcRepositories(basePackages = {"nmng108.microtube.mainservice.repository"}, entityOperationsRef = Constants.BeanName.Database.MainRelationalDatabase.ENTITY_TEMPLATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainR2dbcDatabaseConfiguration extends AbstractR2dbcConfiguration {
    String url;
    String username;
    String password;

    public MainR2dbcDatabaseConfiguration(@Value("${datasource.main-database.url}") String url,
                                          @Value("${datasource.main-database.username}") String username,
                                          @Value("${datasource.main-database.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    @Bean(Constants.BeanName.Database.MainRelationalDatabase.CONNECTION_FACTORY)
    public ConnectionFactory connectionFactory() {
        return ConnectionFactoryBuilder.withUrl(url).username(username).password(password).build();
    }

    @Override
    @Bean(Constants.BeanName.Database.MainRelationalDatabase.DATABASE_CLIENT)
    public DatabaseClient databaseClient() {
        return super.databaseClient();
    }

    @Override
    @Bean(Constants.BeanName.Database.MainRelationalDatabase.ENTITY_TEMPLATE)
    public R2dbcEntityTemplate r2dbcEntityTemplate(
            @Qualifier(Constants.BeanName.Database.MainRelationalDatabase.DATABASE_CLIENT) DatabaseClient databaseClient,
            ReactiveDataAccessStrategy dataAccessStrategy
    ) {
        return super.r2dbcEntityTemplate(databaseClient, dataAccessStrategy);
    }

    @Bean(Constants.BeanName.Database.MainRelationalDatabase.TRANSACTION_MANAGER)
    public R2dbcTransactionManager connectionFactoryTransactionManager() {
        return new R2dbcTransactionManager(connectionFactory());
    }

    @Override
    protected List<Object> getCustomConverters() {
        List<Object> customConverters = new ArrayList<>();

        customConverters.addAll(EntityReadingConverter.getAllConverters);
        customConverters.addAll(EntityWritingConverter.getAllConverters);

        return customConverters;
    }
}
