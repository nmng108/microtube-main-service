package nmng108.microtube.mainservice.configuration;

import lombok.RequiredArgsConstructor;
//import nmng108.microtube.mainservice.entity.User;
//import nmng108.microtube.mainservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AuditorAwareConfig implements ReactiveAuditorAware<Long> {
//    private final UserService userService;

    @Override
    public Mono<Long> getCurrentAuditor() {
        return Mono.just(1L);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // As of current Spring Security version (3.2), authentication is always set with default properties
        // (even when user haven't set it yet):
        // { "authenticated": true, "principle": "anonymousUser", "authorities": [SimpleGrantedAuthority("ROLE_ANONYMOUS")] }
        // Therefore, this "if" predicate should always be false
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return Optional.empty();
//        }
//
//        Object principal = authentication.getPrincipal(); // If unauthenticated, principal = "Anonymous"
//
//        if (principal instanceof User user) {
//            return Optional.of(user.getId());
//        }
//
//        return Optional.empty();
    }

//    @Bean
//    public ReactiveAuditorAware<Long> auditorAware() {
//        return new AuditorAwareConfig(/*userService*/);
//    }

}
