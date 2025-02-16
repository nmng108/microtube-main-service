package nmng108.microtube.mainservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.ErrorCode;
import nmng108.microtube.mainservice.dto.base.ExceptionResponse;
import nmng108.microtube.mainservice.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    /**
     * @param e UsernameNotFoundException
     * @return 401 HTTP status
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ExceptionResponse(ErrorCode.E00006);
    }

    /**
     * An exception caused by user's invalid request data; manually thrown in code
     *
     * @param e InvalidRequestException
     * @return 400
     */
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidRequest(BadRequestException e) {
        return e.toResponse();
    }
}
