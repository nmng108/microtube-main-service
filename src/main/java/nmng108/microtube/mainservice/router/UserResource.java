package nmng108.microtube.mainservice.router;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.user.request.CreateUserDTO;
import nmng108.microtube.mainservice.dto.user.request.UpdateUserDTO;
import nmng108.microtube.mainservice.dto.user.response.UserDTO;
import nmng108.microtube.mainservice.service.UserService;
import nmng108.microtube.mainservice.util.constant.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("${api.base-path}" + Routes.User.basePath)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserResource {
    String basePath;
    UserService userService;

    public UserResource(@Value("${api.base-path}") String basePath, UserService userService) {
        this.basePath = basePath + Routes.User.basePath;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Mono<BaseResponse<List<UserDTO>>>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<UserDTO>>> getUserByIdOrUsername(@PathVariable("id") String identifiable) {
        return ResponseEntity.ok(userService.getUserByIdOrUsername(identifiable));
    }

    @PostMapping
    public Mono<ResponseEntity<BaseResponse<UserDTO>>> createUser(@RequestBody @Valid CreateUserDTO dto) {
        return userService.createUserInfo(dto)
                .map((res) -> {
                    long id = res.getData().getId();

                    return ResponseEntity.created(URI.create(STR."\{basePath}/\{id}")).body(res);
                });
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<UserDTO>>> updateUser(@PathVariable("id") String identifiable, @RequestBody @Valid UpdateUserDTO dto) {
        return ResponseEntity.ok(userService.updateUserInfo(identifiable, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<BaseResponse<Void>>> deleteUser(@PathVariable("id") String identifiable) {
        return ResponseEntity.ok(userService.deleteUser(identifiable));
    }
}
