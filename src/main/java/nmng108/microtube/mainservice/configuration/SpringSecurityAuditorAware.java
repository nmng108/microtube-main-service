package nmng108.microtube.mainservice.configuration;

import lombok.RequiredArgsConstructor;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.service.UserService;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@EnableR2dbcAuditing
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements ReactiveAuditorAware<Long> {
    private final UserService userService;

    @Override
    public Mono<Long> getCurrentAuditor() {
//        return Mono.just(1L);
        return userService.getCurrentUser().map(UserDetailsDTO::getId);
    }
}
