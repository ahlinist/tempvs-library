package club.tempvs.library.controller;

import club.tempvs.library.api.UnauthorizedException;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.RoleRequestService;
import club.tempvs.library.util.AuthHelper;
import club.tempvs.library.util.UserConverter;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LibraryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryController.class);

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthHelper authHelper;
    private final UserConverter userConverter;
    private final LibraryService libraryService;
    private final RoleRequestService roleRequestService;

    @GetMapping("/ping")
    public String getPong() {
        return "pong!";
    }

    @GetMapping("/library")
    public ResponseEntity getWelcomePage(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String token) {
        authHelper.authenticate(token);
        User user = userConverter.convert(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.getWelcomePage(user);
        return ResponseEntity.ok(welcomePageDto);
    }

    @PostMapping("/library/role/{role}")
    public ResponseEntity requestRole(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String token,
            @PathVariable("role") Role role) {
        authHelper.authenticate(token);
        User user = userConverter.convert(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.requestRole(user, role);
        return ResponseEntity.ok(welcomePageDto);
    }


    @DeleteMapping("/library/role/{role}")
    public ResponseEntity cancelRoleRequest(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String token,
            @PathVariable("role") Role role) {
        authHelper.authenticate(token);
        User user = userConverter.convert(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.cancelRoleRequest(user, role);
        return ResponseEntity.ok(welcomePageDto);
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String returnInternalError(Exception e) {
        return processException(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String returnUnauthorized(UnauthorizedException e) {
        return processException(e);
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String returnServiceUnavailable(HystrixRuntimeException e) {
        return processException(e);
    }

    private String processException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTraceString = sw.toString();
        LOGGER.error(stackTraceString);
        return e.getMessage();
    }
}
