package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

public interface UserRepository extends SoftDeletionReactiveRepository<User, Long> {
//    @Query("SELECT EXISTS(SELECT 0 FROM USER u WHERE u.ID = :id AND DELETED_AT IS NULL)")
//    Mono<Boolean> existsById(Long id);

    Mono<User> findByUsername(String username);

    @Query("SELECT u.USERNAME FROM USER u WHERE u.ID = :id")
    Mono<String> findUsernameById(long id);
}
