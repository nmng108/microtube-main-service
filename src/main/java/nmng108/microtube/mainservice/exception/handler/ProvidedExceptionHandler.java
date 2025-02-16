package nmng108.microtube.mainservice.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.ErrorCode;
import nmng108.microtube.mainservice.dto.base.ExceptionResponse;
import nmng108.microtube.mainservice.exception.InternalServerException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.lang.reflect.Field;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProvidedExceptionHandler {
    private static final String MESSAGE_DELIMITER = "$$";

    MessageSource messageSource;

    /**
     * @param e UsernameNotFoundException
     * @return 401 HTTP status
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return null;
//        return new FailureResponse("E11", "User not found");
    }

    /**
     * @param e LockedException
     * @return 403 HTTP status
     */
    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<?> handleAccountBeingLockedException(LockedException e) {
//        this.logError(e);
        return null;
//        return new FailureResponse("E21", "User is locked");
    }

    /**
     * Manually thrown when the resource is not found
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleSpringNoServletResourceFoundException(NoResourceFoundException e, ServerHttpRequest serverHttpRequest) {
        String reason = messageSource.getMessage("errors.resource-path-not-found",
                new Object[]{serverHttpRequest.getPath()}, e.getMessage(), LocaleContextHolder.getLocale());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(ErrorCode.E00004, Collections.singletonList(reason)));
    }

    /**
     * Similar to ResourceNotFoundException.
     *
     * @return 404
     */
    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<ExceptionResponse> handleStoragePathNotFound(NoSuchFileException e) {
//        log.info("({}) {}", e.getClass().getCanonicalName(), e.getMessage());

        return null;
//        return new InvalidRequestException(e.getMessage()).toResponse();
    }

    /**
     * @param e PropertyReferenceException
     * @return 400
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ExceptionResponse> handleMismatchPropertyName(PropertyReferenceException e) {
//        log.info("({}) {}", e.getClass().getCanonicalName(), e.getMessage());

        return null;
//        return new InvalidRequestException(e.getMessage()).toResponse();
    }

//    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
//    public ResponseEntity<ExceptionResponse> handleMismatchPropertyName(InvalidDataAccessApiUsageException e) {
//        log.error("InvalidDataAccessApiUsageException: {}", e.getMessage());
//        return this.handleInvalidRequest(new InvalidRequestException("Wrong field name"));
//    }

    /**
     * Thrown when request data is parsed by DataBinder and violates 1 or several constraints (e.g. Bean Validation).
     *
     * @return 400 - Bad request
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handleDataBindingValidationException(MethodArgumentNotValidException e) {
        String messageDelimiter = messageSource.getMessage("validation.messages.delimiter", new Object[]{}, MESSAGE_DELIMITER, LocaleContextHolder.getLocale());

        // field_name => error_message
        return ResponseEntity.ofNullable(new ExceptionResponse(ErrorCode.E00002, e.getFieldErrors().stream().collect(
                Collectors.toUnmodifiableMap((fieldError) -> {
                    try {
                        Class<?> type = e.getParameter().getParameterType();
                        Field field = type.getDeclaredField(fieldError.getField());

                        return field.getAnnotation(JsonProperty.class).value(); // may throw NullPointerException
                    } catch (NoSuchFieldException innerEx) {
                        throw new RuntimeException(innerEx);
                    } catch (NullPointerException ignored) {
                    }

                    return fieldError.getField();
                }, FieldError::getDefaultMessage, (a, b) -> a + messageDelimiter + b)
        )));
    }

    /**
     * Thrown when request data is only parsed by MessageResolvers and violates 1 or several constraints (e.g. Bean Validation).
     *
     * @return 400 - Bad request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintValidation(ConstraintViolationException e) {
//        log.info("({}) {}", e.getClass().getCanonicalName(), e.getMessage());

        HashMap<String, String> details = new HashMap<>();

        e.getConstraintViolations().forEach((ex) -> {
            String[] fieldPath = ex.getPropertyPath().toString().split("\\.");

            log.info(ex.getConstraintDescriptor().getComposingConstraints().toString());

            if (fieldPath.length > 0) {
                details.put(fieldPath[fieldPath.length - 1], ex.getMessage());
            }
        });

        return ResponseEntity.badRequest().body(new ExceptionResponse(ErrorCode.E00001, details));
    }

    /**
     * An exception happens when request body format does not follow the standard (e.g. JSON)
     * or parser cannot parse Enum/Date fields, ....
     *
     * @param e HttpMessageNotReadableException
     * @return 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleUnreadableRequest(HttpMessageNotReadableException e) throws Exception {
        return ResponseEntity.badRequest().body(new ExceptionResponse(ErrorCode.E00001));
    }

//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        return ResponseEntity.badRequest().body(new ExceptionResponse(ErrorCode.E00000));
//    }

    /**
     * Catch any exception other than the exceptions declared above.
     * <p>
     * By default, we consider all other exceptions are Internal server errors.
     *
     * @return 500 - Internal server error
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleCommonExceptions(Exception e) {
//        log.info("({}) {}", e.getClass().getCanonicalName(), e.getMessage());
        // trace error
        e.printStackTrace();

        return new InternalServerException(e.getMessage()).toResponse();
    }
}
