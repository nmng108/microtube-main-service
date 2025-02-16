package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.user.request.CreateUserDTO;
import nmng108.microtube.mainservice.dto.user.request.UpdateUserDTO;
import nmng108.microtube.mainservice.dto.user.response.UserDTO;
import nmng108.microtube.mainservice.entity.User;
import nmng108.microtube.mainservice.repository.UserRepository;
import nmng108.microtube.mainservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public Mono<BaseResponse<List<UserDTO>>> getAllUsers() {
        return userRepository.findAll().log()
                .map(UserDTO::new)
                .collectList()
                .map(BaseResponse::succeeded);
    }

    @Override
    public Mono<BaseResponse<UserDTO>> getUserByIdOrUsername(String identifiable) {
        return retrieveUser(identifiable)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .mapNotNull(UserDTO::new)
                .mapNotNull(BaseResponse::succeeded)
                ;
    }

    @Override
    public Mono<BaseResponse<UserDTO>> createUserInfo(CreateUserDTO dto) {
        return userRepository.save(dto.toUser()).log()
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
        return Mono.fromCallable(() -> Long.parseLong(identifiable)).log()
                .flatMap(userRepository::findById)
                .onErrorResume(NumberFormatException.class, (error) -> userRepository.findByUsername(identifiable));
    }
}
