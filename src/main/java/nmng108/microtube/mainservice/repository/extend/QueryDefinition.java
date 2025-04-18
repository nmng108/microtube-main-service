package nmng108.microtube.mainservice.repository.extend;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QueryDefinition<R> {
    Flux<R> find();
    Mono<Long> countTotal();
}
