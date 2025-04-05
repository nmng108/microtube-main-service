package nmng108.microtube.mainservice.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.exception.ForbiddenException;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityExceptionHandlerConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public ServerAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return (exchange, authenticationException) -> {
            ServerHttpResponse response = exchange.getResponse();

            log.info("Run into jwtAuthenticationEntryPoint. Exception: {}, reason: {}", authenticationException.getClass().getName(), authenticationException.getMessage());
            return ReactiveSecurityContextHolder.getContext()
                    .doOnNext((securityContext) -> log.info("Denied authentication: {}", securityContext.getAuthentication()))
                    .map((context) -> {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

                        return context;
                    })
                    .switchIfEmpty(Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                    .then(Mono.fromCallable(() -> {
                        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                        return objectMapper.writeValueAsBytes(new UnauthorizedException().toResponse().getBody());
                    }))
                    .flatMap((bytes) -> {
                        DataBuffer buffer = response.bufferFactory().wrap(bytes);

                        return response.writeWith(Mono.just(buffer)).doOnError((error) -> DataBufferUtils.release(buffer));
                    })
                    .then();

//        return Mono.fromRunnable(() -> {
//            exchange.getResponse().setStatusCode(httpStatus);
//            exchange.getResponse().getHeaders().set("X-Error", authenticationException.getMessage());
//        });
        };
    }

    @Bean
    public ServerAccessDeniedHandler jwtAccessDeniedHandler() {
        return (exchange, deniedException) -> {
            ServerHttpResponse response = exchange.getResponse();

            log.info("Run into jwtAccessDeniedHandler. Exception: {}, reason: {}", deniedException.getClass().getName(), deniedException.getMessage());
            return Mono.fromCallable(() -> {
                        response.setStatusCode(HttpStatus.FORBIDDEN);
                        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                        return objectMapper.writeValueAsBytes(new ForbiddenException().toResponse().getBody());
                    })
                    .flatMap((bytes) -> {
                        DataBuffer buffer = response.bufferFactory().wrap(bytes);

                        return response.writeWith(Mono.just(buffer)).doOnError((error) -> DataBufferUtils.release(buffer));
                    })
                    .then();
        };
    }
}