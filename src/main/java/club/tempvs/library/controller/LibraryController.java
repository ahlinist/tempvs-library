package club.tempvs.library.controller;

import club.tempvs.library.api.ForbiddenException;
import club.tempvs.library.api.UnauthorizedException;
import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.UserService;
import club.tempvs.library.util.AuthHelper;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LibraryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryController.class);

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String DEFAULT_PAGE_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "40";
    private static final int MAX_PAGE_SIZE = 40;

    private final AuthHelper authHelper;
    private final LibraryService libraryService;
    private final UserService userService;

    @GetMapping("/ping")
    public String getPong() {
        return "pong!";
    }

    @GetMapping("/library")
    public ResponseEntity getWelcomePage(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(AUTHORIZATION_HEADER) String token) {
        authHelper.authenticate(token);
        User user = new User(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.getWelcomePage(user);
        return ResponseEntity.ok(welcomePageDto);
    }

    @PostMapping("/library/role/{role}")
    public ResponseEntity requestRole(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(AUTHORIZATION_HEADER) String token,
            @PathVariable("role") Role role) {
        authHelper.authenticate(token);
        User user = new User(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.requestRole(user, role);
        return ResponseEntity.ok(welcomePageDto);
    }


    @DeleteMapping("/library/role/{role}")
    public ResponseEntity cancelRoleRequest(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(AUTHORIZATION_HEADER) String token,
            @PathVariable("role") Role role) {
        authHelper.authenticate(token);
        User user = new User(userInfoDto);
        libraryService.deleteRoleRequest(user, role);
        WelcomePageDto welcomePageDto = libraryService.getWelcomePage(user);
        return ResponseEntity.ok(welcomePageDto);
    }

    @GetMapping("/library/admin")
    public ResponseEntity getAdminPanelPage(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(AUTHORIZATION_HEADER) String token,
            @RequestParam(value = PAGE_PARAM, required = false, defaultValue = DEFAULT_PAGE_VALUE) int page,
            @RequestParam(value = SIZE_PARAM, required = false, defaultValue = DEFAULT_SIZE_VALUE) int size) {
        authHelper.authenticate(token);
        User user = new User(userInfoDto);
        List<Role> roles = user.getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not be larger than " + MAX_PAGE_SIZE + "!");
        }

        LocaleContextHolder.setLocale(user.getLocale());
        AdminPanelPageDto adminPanelPageDto = libraryService.getAdminPanelPage(page, size);
        return ResponseEntity.ok(adminPanelPageDto);
    }

    @DeleteMapping("/library/{role}/{userId}")
    public ResponseEntity denyRoleRequest(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestHeader(AUTHORIZATION_HEADER) String token,
            @RequestParam(value = PAGE_PARAM, required = false, defaultValue = DEFAULT_PAGE_VALUE) int page,
            @RequestParam(value = SIZE_PARAM, required = false, defaultValue = DEFAULT_SIZE_VALUE) int size,
            @PathVariable("role") Role role,
            @PathVariable("userId") Long userId) {
        authHelper.authenticate(token);
        User adminUser = new User(userInfoDto);
        List<Role> roles = adminUser.getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not be larger than " + MAX_PAGE_SIZE + "!");
        }

        LocaleContextHolder.setLocale(adminUser.getLocale());
        User user = userService.getUser(userId);
        libraryService.deleteRoleRequest(user, role);
        AdminPanelPageDto adminPanelPageDto = libraryService.getAdminPanelPage(page, size);
        return ResponseEntity.ok(adminPanelPageDto);
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

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String returnForbidden(ForbiddenException e) {
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
