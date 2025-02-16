package nmng108.microtube.mainservice.repository.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.entity.Accountable;
import nmng108.microtube.mainservice.repository.SoftDeletionReactiveRepository;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.util.Lazy;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SoftDeletionReactiveRepositoryImpl<T extends Accountable, ID> extends SimpleR2dbcRepository<T, ID> implements SoftDeletionReactiveRepository<T, ID> {
    RelationalEntityInformation<T, ID> entity;
    R2dbcEntityOperations entityOperations;
    Lazy<RelationalPersistentProperty> idProperty;
    Lazy<RelationalPersistentProperty> deletedAtProperty;
//    RelationalExampleMapper exampleMapper;

    public SoftDeletionReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
        super(entity, entityOperations, converter);

        this.entity = entity;
        this.entityOperations = entityOperations;
        this.idProperty = Lazy.of(() -> {
            return (RelationalPersistentProperty) ((RelationalPersistentEntity) converter.getMappingContext().getRequiredPersistentEntity(this.entity.getJavaType())).getRequiredIdProperty();
        });
        this.deletedAtProperty = Lazy.of(() -> {
            return (RelationalPersistentProperty) ((RelationalPersistentEntity) converter.getMappingContext().getRequiredPersistentEntity(this.entity.getJavaType())).getPersistentProperty("deletedAt");
        });
//        this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
    }

    public SoftDeletionReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, DatabaseClient databaseClient, R2dbcConverter converter, ReactiveDataAccessStrategy accessStrategy) {
        super(entity, databaseClient, converter, accessStrategy);

        this.entity = entity;
        this.entityOperations = new R2dbcEntityTemplate(databaseClient, accessStrategy);
        this.idProperty = Lazy.of(() -> {
            return (RelationalPersistentProperty) ((RelationalPersistentEntity) converter.getMappingContext().getRequiredPersistentEntity(this.entity.getJavaType())).getRequiredIdProperty();
        });
        this.deletedAtProperty = Lazy.of(() -> {
            return (RelationalPersistentProperty) ((RelationalPersistentEntity) converter.getMappingContext().getRequiredPersistentEntity(this.entity.getJavaType())).getPersistentProperty("deletedAt");
        });
//        this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
    }

    @Override
    public Mono<T> findById(ID id) {
        Assert.notNull(id, "Id must not be null");

        return this.entityOperations.selectOne(this.getIdQuery(id), this.entity.getJavaType());
    }

    @Override
    public Mono<T> findById(Publisher<ID> publisher) {
        return super.findById(publisher);
//        return Mono.from(publisher).flatMap(this::findById);
    }

    @Override
    public Mono<Boolean> existsById(ID id) {
        Assert.notNull(id, "Id must not be null");

        return this.entityOperations.exists(this.getIdQuery(id), this.entity.getJavaType());
    }

    @Override
    public Mono<Boolean> existsById(Publisher<ID> publisher) {
        return super.existsById(publisher);
//        return Mono.from(publisher).flatMap(this::findById).hasElement();
    }

    @Override
    public Flux<T> findAll() {
        return this.entityOperations.select(Query.query(Criteria.where(this.getDeletedAtProperty().getName()).isNull()), this.entity.getJavaType());
    }

    @Override
    public Flux<T> findAll(Sort sort) {
        Assert.notNull(sort, "Sort must not be null");

        return this.entityOperations.select(Query.query(Criteria.where(this.getDeletedAtProperty().getName()).isNull()).sort(sort), this.entity.getJavaType());
    }

    @Override
    public Flux<T> findAllById(Iterable<ID> iterable) {
        Assert.notNull(iterable, "The iterable of Id's must not be null");

        return this.findAllById(Flux.fromIterable(iterable));
    }

    @Override
    public Flux<T> findAllById(Publisher<ID> idPublisher) {
        Assert.notNull(idPublisher, "The Id Publisher must not be null");

        return Flux.from(idPublisher).buffer().filter((ids) -> {
            return !ids.isEmpty();
        }).concatMap((ids) -> {
            if (ids.isEmpty()) {
                return Flux.empty();
            } else {
                String idProperty = this.getIdProperty().getName();

                return this.entityOperations.select(Query.query(Criteria.where(idProperty).in(ids).and(this.getDeletedAtProperty().getName()).isNull()), this.entity.getJavaType());
            }
        });
    }

    @Override
    public Mono<Long> count() {
        return this.entityOperations.count(Query.query(Criteria.where(this.getDeletedAtProperty().getName()).isNull()), this.entity.getJavaType());
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(ID id) {
        Assert.notNull(id, "Id must not be null");

        return this.entityOperations.update(this.getIdQuery(id), Update.update("DELETED_AT", LocalDateTime.now()), this.entity.getJavaType()).then();
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(Publisher<ID> idPublisher) {
        Assert.notNull(idPublisher, "The Id Publisher must not be null");

        return Flux.from(idPublisher).buffer()
                .filter((ids) -> !ids.isEmpty())
                .flatMap((ids) -> this.entityOperations.update(this.getIdsQuery(ids), Update.update("DELETED_AT", LocalDateTime.now()), this.entity.getJavaType()))
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> delete(T objectToDelete) {
        return super.delete(objectToDelete);
//        Assert.notNull(objectToDelete, "Object to delete must not be null");
//
//        return this.deleteById(this.entity.getRequiredId(objectToDelete));
    }

    @Override
    @Transactional
    public Mono<Void> deleteAllById(Iterable<? extends ID> ids) {
        Assert.notNull(ids, "The iterable of Id's must not be null");

        return this.deleteById(Flux.fromIterable(ids));
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll(Iterable<? extends T> iterable) {
        return super.deleteAll(iterable);
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll(Publisher<? extends T> objectPublisher) {
        return super.deleteAll(objectPublisher);
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll() {
        return super.deleteAll();
    }

    @Override
    public <S extends T> Mono<S> findOne(Example<S> example) {
        return Mono.error(new UnsupportedOperationException());
    }

    @Override
    public <S extends T> Flux<S> findAll(Example<S> example) {
        return Flux.error(new UnsupportedOperationException());
    }

    @Override
    public <S extends T> Flux<S> findAll(Example<S> example, Sort sort) {
        return Flux.error(new UnsupportedOperationException());
    }

    @Override
    public <S extends T> Mono<Long> count(Example<S> example) {
        return Mono.error(new UnsupportedOperationException());
    }

    @Override
    public <S extends T> Mono<Boolean> exists(Example<S> example) {
        return Mono.error(new UnsupportedOperationException());
    }

    @Override
    public <S extends T, R, P extends Publisher<R>> P findBy(Example<S> example, Function<FluentQuery.ReactiveFluentQuery<S>, P> queryFunction) {
        return (P) Mono.error(new UnsupportedOperationException());
    }

    private RelationalPersistentProperty getIdProperty() {
        return this.idProperty.get();
    }

    private RelationalPersistentProperty getDeletedAtProperty() {
        return this.deletedAtProperty.get();
    }

    private CriteriaDefinition getNotDeletedPredicate() {
        return Criteria.where(this.getDeletedAtProperty().getName()).isNull();
    }

    private Query getIdQuery(Object id) {
        return Query.query(Criteria.where(this.getIdProperty().getName()).is(id).and(this.getNotDeletedPredicate()));
    }

    private Query getIdsQuery(List<? extends ID> ids) {
        return Query.query(Criteria.where(this.getIdProperty().getName()).in(ids).and(this.getNotDeletedPredicate()));
    }
}
