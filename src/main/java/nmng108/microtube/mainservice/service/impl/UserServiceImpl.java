package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.user.request.CreateUserDTO;
import nmng108.microtube.mainservice.dto.user.request.UpdateUserDTO;
import nmng108.microtube.mainservice.dto.user.response.UserDTO;
import nmng108.microtube.mainservice.entity.User;
import nmng108.microtube.mainservice.repository.UserRepository;
import nmng108.microtube.mainservice.service.UserService;
import nmng108.microtube.mainservice.util.constant.Constants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(Constants.BeanName.Database.MainRelationalDatabase.TRANSACTION_MANAGER)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<BaseResponse<List<UserDTO>>> getAllUsers() {
        return userRepository.findAll()
                .map(UserDTO::new)
                .collectList()
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<UserDTO>> getUserByIdOrUsername(String identifiable) {
        return retrieveUser(identifiable)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .mapNotNull(UserDTO::new)
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public Mono<UserDetailsDTO> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                // As of current Spring Security version (3.2), authentication is always set with default properties
                // (even when user haven't set it yet):
                // { "authenticated": true, "principle": "anonymousUser", "authorities": [SimpleGrantedAuthority("ROLE_ANONYMOUS")] }
                // Therefore, this predicate must be false in case of anonymous access
                .filter((authentication) -> !(authentication instanceof AnonymousAuthenticationToken))
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .cast(UserDetailsDTO.class);
    }

    @Override
    public Mono<BaseResponse<UserDTO>> createUserInfo(CreateUserDTO dto) {
        return userRepository.save(dto.toUser())
                .mapNotNull(UserDTO::new)
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<UserDTO>> updateUserInfo(String identifiable, UpdateUserDTO dto) {
        return retrieveUser(identifiable)
                .flatMap((c) -> {
                    Optional.ofNullable(dto.getName()).ifPresent(c::setName);
                    Optional.ofNullable(dto.getEmail()).ifPresent(c::setEmail);
                    Optional.ofNullable(dto.getPhoneNumber()).ifPresent(c::setPhoneNumber);

                    return userRepository.save(c);
                })
                .mapNotNull(UserDTO::new)
                .mapNotNull(BaseResponse::succeeded)
                ;
    }

    @Override
    public Mono<BaseResponse<Void>> deleteUser(String identifiable) {
        return retrieveUser(identifiable)
                .flatMap(userRepository::delete)
                .then(Mono.fromCallable(BaseResponse::succeeded));
    }

    public Mono<User> retrieveUser(String identifiable) {
        return Mono.fromCallable(() -> Long.parseLong(identifiable))
                .flatMap(userRepository::findById)
                .onErrorResume(NumberFormatException.class, (error) -> userRepository.findByUsername(identifiable));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).cast(UserDetails.class);
    }
}
