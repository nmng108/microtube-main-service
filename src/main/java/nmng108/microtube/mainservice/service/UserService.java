package nmng108.microtube.mainservice.service;

import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.user.request.CreateUserDTO;
import nmng108.microtube.mainservice.dto.user.request.UpdateUserDTO;
import nmng108.microtube.mainservice.dto.user.response.UserDTO;
import nmng108.microtube.mainservice.entity.User;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService extends ReactiveUserDetailsService {
    Mono<BaseResponse<List<UserDTO>>> getAllUsers();

    Mono<BaseResponse<UserDTO>> getUserByIdOrUsername(String identifiable);

    Mono<UserDetailsDTO> getCurrentUser();

    Mono<BaseResponse<UserDTO>> createUserInfo(CreateUserDTO dto);

    Mono<BaseResponse<UserDTO>> updateUserInfo(String identifiable, UpdateUserDTO dto);

    Mono<BaseResponse<Void>> deleteUser(String identifiable);
}