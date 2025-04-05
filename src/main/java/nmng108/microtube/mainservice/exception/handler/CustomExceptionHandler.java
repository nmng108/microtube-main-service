package nmng108.microtube.mainservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.ErrorCode;
import nmng108.microtube.mainservice.dto.base.ExceptionResponse;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.exception.ForbiddenException;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    /**
     * An exception caused by user's invalid request data, which is manually thrown in code.
     *
     * @return 400
     */
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidRequest(BadRequestException e) {
        return e.toResponse();
    }

    /**
     * @return 403
     */
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        log.info("Manually thrown UnauthorizedException");
        return e.toResponse();
    }

    /**
     * @return 403
     */
    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException e) {
        return e.toResponse();
    }
}
