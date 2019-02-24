package club.tempvs.library.exception.handler;

import static org.springframework.http.HttpStatus.*;

import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.exception.UnauthorizedException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public String returnInternalError(Exception e) {
        return processException(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public String returnUnauthorized(UnauthorizedException e) {
        return processException(e);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(FORBIDDEN)
    public String returnForbidden(ForbiddenException e) {
        return processException(e);
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public String returnServiceUnavailable(HystrixRuntimeException e) {
        return processException(e);
    }

    private String processException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTraceString = sw.toString();
        log.error(stackTraceString);
        return e.getMessage();
    }
}
