package nmng108.microtube.mainservice.configuration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    private final static String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    JwtUtils jwtUtils;
    ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!hasValidAuthorizationBearer(authorizationHeader)) {
            return Mono.empty();
        }

        String token = getAccessToken(authorizationHeader);

        return jwtUtils.parseSignedClaims(token)
                .map(Mono::just)
                .orElse(Mono.empty())
                .flatMap((jwt) -> this.getUserDetails(jwt)
                        .filter(UserDetails::isAccountNonLocked)
                        .switchIfEmpty(Mono.fromRunnable(() -> log.info("User is still being locked. Continue using service as an anonymous."))))
                .map((userDetails) -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
                .map(SecurityContextImpl::new);
    }

    private boolean hasValidAuthorizationBearer(@Nullable String header) {
        return header != null && header.matches(STR."^\{AUTHORIZATION_HEADER_PREFIX}[\\w-]+(\\.[\\w-]){2}$");
    }

    private String getAccessToken(String header) {
        return header.split(AUTHORIZATION_HEADER_PREFIX)[1].trim();
    }

    private Mono<UserDetails> getUserDetails(Jws<Claims> claimsJws) {
        String username = claimsJws.getPayload().getSubject();

        return Mono.from(this.userDetailsService.findByUsername(username))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")));
    }
}
