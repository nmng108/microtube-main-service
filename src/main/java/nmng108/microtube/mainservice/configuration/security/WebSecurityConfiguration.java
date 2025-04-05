package nmng108.microtube.mainservice.configuration.security;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSecurityConfiguration {
    ServerAuthenticationEntryPoint authenticationEntryPoint;
    ServerAccessDeniedHandler accessDeniedHandler;
    //    JwtVerificationFilter jwtVerificationFilter;
    CentralAuthSecurityContextRepository centralAuthSecurityContextRepository;
    String apiBasePath;

    public WebSecurityConfiguration(ServerAuthenticationEntryPoint authenticationEntryPoint,
                                    ServerAccessDeniedHandler accessDeniedHandler,
//                                    JwtVerificationFilter jwtVerificationFilter,
                                    CentralAuthSecurityContextRepository centralAuthSecurityContextRepository,
                                    @Value("${api.base-path}") String apiBasePath
    ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
//        this.jwtVerificationFilter = jwtVerificationFilter;
        this.centralAuthSecurityContextRepository = centralAuthSecurityContextRepository;
        this.apiBasePath = apiBasePath;
    }

//    @Bean // for test purpose
//    UserDetailsService userDetailsService() {
//        MapUserDetailsManager manager = new InMemoryUserDetailsManager();
//
//        manager.createUser(User.builder().username("lib").password("1").roles(Role.LIBRARIAN).build());
//        manager.createUser(User.builder().username("patron").password("2").roles("PATRON").build());
//
//        return manager;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    public ReactiveAuthenticationManager authenticationManager() {
//        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//    }

//    @Bean
//    public FilterRegistrationBean<JwtVerificationFilter> registerJwtFilter(JwtVerificationFilter filter) {
//        FilterRegistrationBean<JwtVerificationFilter> registrationBean = new FilterRegistrationBean<>(filter);
//
//        registrationBean.setEnabled(false);
//
//        return registrationBean;
//    }

//    @Bean
//    public SecurityFilterChain authenticationEndpointFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .securityMatcher(AntPathRequestMatcher.antMatcher(serverBasePath + Routes.Auth.basePath + "/**"))
//                .exceptionHandling((handler) -> handler.authenticationEntryPoint(this.authenticationEntryPoint))
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers(AntPathRequestMatcher.antMatcher(Routes.Auth.login)).anonymous()
//                        .requestMatchers(AntPathRequestMatcher.antMatcher(Routes.Auth.register)).permitAll()
//                        .requestMatchers(AntPathRequestMatcher.antMatcher(Routes.Auth.forgot)).permitAll()
//                        .anyRequest().permitAll()
//                )
//                .addFilterBefore(this.jwtVerificationFilter, UsernamePasswordAuthenticationFilter.class)
//                .userDetailsService(this.userDetailsService)
//        ;
//
//        return httpSecurity.build();
//    }

//    @Bean
//    public SecurityFilterChain userManagementEndpointFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .securityMatcher(serverBasePath + Routes.users + "/**")
//                .exceptionHandling((handler) -> handler.authenticationEntryPoint(this.authenticationEntryPoint))
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests((authorize) -> authorize
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(this.jwtVerificationFilter, AuthorizationFilter.class)
////                .userDetailsService(this.userDetailsService)
//        ;
//
//        return httpSecurity.build();
//    }

    @Bean
    public SecurityWebFilterChain channelEndpointsFilterChain(ServerHttpSecurity serverHttpSecurity) throws Exception {
        return serverHttpSecurity
                .securityMatcher((exchange) -> exchange.getRequest().getPath().value().matches(STR."^\{apiBasePath}/channels.*$")
                        ? ServerWebExchangeMatcher.MatchResult.match()
                        : ServerWebExchangeMatcher.MatchResult.notMatch())
                .exceptionHandling((handler) -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((authorize) -> authorize
                        .pathMatchers(HttpMethod.GET, apiBasePath + "/channels").permitAll()
                        .pathMatchers(HttpMethod.GET, apiBasePath + "/channels/**").permitAll()
                        .pathMatchers(HttpMethod.POST, apiBasePath + "/channels").authenticated()
                        .pathMatchers(HttpMethod.PATCH, apiBasePath + "/channels/*").authenticated()
                        .pathMatchers(HttpMethod.DELETE, apiBasePath + "/channels/*").authenticated()
                        .anyExchange().authenticated()
                )
                .securityContextRepository(centralAuthSecurityContextRepository)
//                .authenticationManager(authenticationManager())
//                .addFilterAfter(jwtVerificationFilter, SecurityWebFiltersOrder.LAST)
                .build();
    }

    @Bean
    public SecurityWebFilterChain videoEndpointsFilterChain(ServerHttpSecurity serverHttpSecurity) throws Exception {
        return serverHttpSecurity
                .securityMatcher((exchange) -> exchange.getRequest().getPath().value().matches(STR."^\{apiBasePath}/videos.*$")
                        ? ServerWebExchangeMatcher.MatchResult.match()
                        : ServerWebExchangeMatcher.MatchResult.notMatch())
                .exceptionHandling((handler) -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((authorize) -> authorize
                        .pathMatchers(HttpMethod.GET, apiBasePath + "/videos").permitAll()
                        .pathMatchers(HttpMethod.GET, apiBasePath + "/videos/**").permitAll()
//                        .pathMatchers(HttpMethod.POST, apiBasePath + "/videos").authenticated()
//                        .pathMatchers(HttpMethod.PATCH, apiBasePath + "/videos/*").authenticated()
//                        .pathMatchers(HttpMethod.DELETE, apiBasePath + "/videos/*").authenticated()
                        .anyExchange().authenticated()
                )
                .securityContextRepository(centralAuthSecurityContextRepository)
                .build();
    }
}