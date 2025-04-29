package nmng108.microtube.mainservice.configuration;

import lombok.RequiredArgsConstructor;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
@EnableR2dbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "auditingDateTimeProvider")
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements ReactiveAuditorAware<Long> {
    private final UserService userService;

    @Override
    public Mono<Long> getCurrentAuditor() {
//        return Mono.just(1L);
        return userService.getCurrentUser().map(UserDetailsDTO::getId);
    }

    @Bean("auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC)); // Replace with your desired ZoneId
    }
}
