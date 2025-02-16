package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Accountable;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface SoftDeletionReactiveRepository<T extends Accountable, ID> extends Repository<T, ID> {
    <S extends T> Mono<S> save(S entity);

    <S extends T> Flux<S> saveAll(Iterable<S> entities);

    <S extends T> Flux<S> saveAll(Publisher<S> entityStream);

    Mono<T> findById(ID id);

    Mono<T> findById(Publisher<ID> id);

    Mono<Boolean> existsById(ID id);

    Mono<Boolean> existsById(Publisher<ID> id);

    Flux<T> findAll();

    Flux<T> findAll(Sort sort);

    Flux<T> findAllById(Iterable<ID> ids);

    Flux<T> findAllById(Publisher<ID> idStream);

    Mono<Long> count();

    Mono<Void> deleteById(ID id);

    Mono<Void> deleteById(Publisher<ID> id);

    Mono<Void> delete(T entity);

    Mono<Void> deleteAllById(Iterable<? extends ID> ids);

    Mono<Void> deleteAll(Iterable<? extends T> entities);

    Mono<Void> deleteAll(Publisher<? extends T> entityStream);

    Mono<Void> deleteAll();
}
