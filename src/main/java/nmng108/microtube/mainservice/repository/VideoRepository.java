package nmng108.microtube.mainservice.repository;

import nmng108.microtube.mainservice.entity.Video;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface VideoRepository extends ReactiveCrudRepository<Video, Long> {

}
