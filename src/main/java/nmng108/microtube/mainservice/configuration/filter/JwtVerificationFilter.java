//package nmng108.microtube.mainservice.configuration.filter;
//
//import com.auth0.jwt.interfaces.DecodedJWT;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.micrometer.common.lang.NonNullApi;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import nmng108.microtube.mainservice.configuration.security.JwtUtils;
//import nmng108.microtube.mainservice.repository.UserRepository;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.lang.Nullable;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@NonNullApi
//public class JwtVerificationFilter implements WebFilter {
//    private final static String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
//
//    JwtUtils jwtUtils;
//    ReactiveUserDetailsService userDetailsService;
//    UserRepository userRepository;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain filterChain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        if (!hasValidAuthorizationBearer(header)) {
//            return filterChain.filter(exchange);
//        }
//
//        String token = getAccessToken(header);
//
//        // check if user is locked. If not, then Authentication will be saved to the security context
////        if (userDetails.isAccountNonLocked()) {
////            setAuthenticationContext(userDetails, request);
////        }/* else if (userDetails instanceof User && LocalDateTime.now().isAfter(((User) userDetails).getLockExpirationDate())) { // should always be true
////            ((User) userDetails).setLocked(false);
////            this.userRepository.save((User) userDetails);
////            log.info("Unlocked user. Continue using service.");
////
////            setAuthenticationContext(userDetails, request);
////        }*/ else {
////            log.info("User is still being locked. Continue using service as an anonymous.");
////        }
//log.info("token: {}", token);
////log.info("principle: {}", ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(Authentication::getPrincipal).block());
//        return jwtUtils.parseSignedClaims(token)
////        return jwtUtils.decode(token)
//                .map(Mono::just)
//                .orElse(Mono.empty())
//                .flatMap((jwt) -> this.getUserDetails(jwt)
//                        .filter(UserDetails::isAccountNonLocked)
//                        .switchIfEmpty(Mono.fromRunnable(() -> log.info("User is still being locked. Continue using service as an anonymous."))))
//                .flatMap((userDetails) -> setAuthenticationContext(userDetails, request))
//                .then(filterChain.filter(exchange));
//    }
//
//    private boolean hasValidAuthorizationBearer(@Nullable String header) {
//        return header != null && header.matches(STR."^\{AUTHORIZATION_HEADER_PREFIX}[.0-9a-zA-Z_-]+");
//    }
//
//    private String getAccessToken(String header) {
//        return header.split(AUTHORIZATION_HEADER_PREFIX)[1].trim();
//    }
//
//    private Mono<UserDetails> getUserDetails(Jws<Claims> claimsJws) {
//        String username = claimsJws.getPayload().getSubject();
//
//        return Mono.from(this.userDetailsService.findByUsername(username))
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")));
//    }
//
//    private Mono<UserDetails> getUserDetails(DecodedJWT decodedJWT) {
//        String username = decodedJWT.getSubject();
//
//        return Mono.from(this.userDetailsService.findByUsername(username))
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")));
//    }
//
//    private Mono<Void> setAuthenticationContext(UserDetails userDetails, ServerHttpRequest request) {
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
////        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        log.info("authorities: {}", authentication.getAuthorities());
//        return ReactiveSecurityContextHolder.getContext()
////                .doOnNext(context -> context.setAuthentication(authentication))
////                .switchIfEmpty(Mono.just(new SecurityContextImpl(authentication)).doOnNext(ReactiveSecurityContextHolder.withSecurityContext()))
//                .then();
//    }
//
////    private void changeLockStatus()
//}