package nmng108.microtube.mainservice.configuration.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.WebClientResponseBody;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Primary
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CentralAuthSecurityContextRepository implements ServerSecurityContextRepository {
    private final static String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    WebClient webClient;
    ReactiveUserDetailsService userDetailsService;

    HttpMethod authServiceUserDetailsFetchingMethod;
    String authServiceUserDetailsFetchingPath;

    public CentralAuthSecurityContextRepository(
            WebClient webClient,
            ReactiveUserDetailsService userDetailsService,
            @Value("${rest-service.auth.name}") String authServiceName,
            @Value("${rest-service.auth.api.fetch-user-info.method:GET}") String authServiceApiUserDetailsFetchingMethod,
            @Value("${rest-service.auth.api.fetch-user-info.path}") String authServiceApiUserDetailsFetchingPath
    ) {
        this.webClient = webClient;
        this.userDetailsService = userDetailsService;
        this.authServiceUserDetailsFetchingMethod = HttpMethod.valueOf(authServiceApiUserDetailsFetchingMethod.toUpperCase());
        this.authServiceUserDetailsFetchingPath = STR."lb://\{authServiceName}\{authServiceApiUserDetailsFetchingPath}";
    }

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

//        return jwtUtils.decode(getAccessToken(header)) // read passed token without verification; for test purpose
//                .map(Mono::just)
//                .orElse(Mono.empty())
//                .flatMap((jwt) -> this.getUserDetails(jwt)
//                .filter(UserDetails::isAccountNonLocked)
//                .switchIfEmpty(Mono.fromRunnable(() -> log.info("User is still being locked. Continue using service as an anonymous."))))
        return webClient.method(authServiceUserDetailsFetchingMethod).uri(authServiceUserDetailsFetchingPath)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
//                .exchangeToMono((clientResponse) -> clientResponse.bodyToMono(Map.class) // read full body; for test purpose
                .exchangeToMono((clientResponse) -> clientResponse.bodyToMono(new ParameterizedTypeReference<WebClientResponseBody<UserDetailsDTO>>(){})
                        .filter((_) -> clientResponse.statusCode().is2xxSuccessful()))
                .map(WebClientResponseBody::getData)
                .map((userDetails) -> new SecurityContextImpl(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                ))
                ;
    }

    private boolean hasValidAuthorizationBearer(@Nullable String header) {
        return header != null && header.matches(STR."^\{AUTHORIZATION_HEADER_PREFIX}[\\w-]+(\\.[\\w-]+){2}$");
    }

    private String getAccessToken(String header) {
        return header.split(AUTHORIZATION_HEADER_PREFIX)[1].trim();
    }

    private Mono<UserDetails> getUserDetails(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();

        return Mono.from(this.userDetailsService.findByUsername(username))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")));
    }
}
