package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);

    @Query("SELECT u.USERNAME FROM USER u WHERE u.ID = :id")
    Mono<String> findUsernameById(long id);
}
