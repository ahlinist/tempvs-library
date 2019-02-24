package club.tempvs.library.controller;

import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LibraryController {

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String DEFAULT_PAGE_PARAM = "0";
    private static final int DEFAULT_PAGE_VALUE = 0;
    private static final String DEFAULT_SIZE_PARAM = "40";
    private static final int DEFAULT_SIZE_VALUE = 40;
    private static final int MAX_PAGE_SIZE = 40;

    private final LibraryService libraryService;
    private final UserService userService;

    @GetMapping("/library")
    public ResponseEntity getWelcomePage(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto) {
        User user = new User(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.getWelcomePage(user);
        return ResponseEntity.ok(welcomePageDto);
    }

    @PostMapping("/library/role/{role}")
    public ResponseEntity requestRole(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @PathVariable("role") Role role) {
        User user = new User(userInfoDto);
        WelcomePageDto welcomePageDto = libraryService.requestRole(user, role);
        return ResponseEntity.ok(welcomePageDto);
    }


    @DeleteMapping("/library/role/{role}")
    public ResponseEntity cancelRoleRequest(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @PathVariable("role") Role role) {
        User user = new User(userInfoDto);
        libraryService.deleteRoleRequest(user, role);
        WelcomePageDto welcomePageDto = libraryService.getWelcomePage(user);
        return ResponseEntity.ok(welcomePageDto);
    }

    @GetMapping("/library/admin")
    public ResponseEntity getAdminPanelPage(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @RequestParam(value = PAGE_PARAM, required = false, defaultValue = DEFAULT_PAGE_PARAM) int page,
            @RequestParam(value = SIZE_PARAM, required = false, defaultValue = DEFAULT_SIZE_PARAM) int size) {
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
            @PathVariable("role") Role role,
            @PathVariable("userId") Long userId) {
        User adminUser = new User(userInfoDto);
        List<Role> roles = adminUser.getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        LocaleContextHolder.setLocale(adminUser.getLocale());
        User user = userService.getUser(userId);
        libraryService.deleteRoleRequest(user, role);
        AdminPanelPageDto adminPanelPageDto = libraryService.getAdminPanelPage(DEFAULT_PAGE_VALUE, DEFAULT_SIZE_VALUE);
        return ResponseEntity.ok(adminPanelPageDto);
    }

    @PostMapping("/library/{role}/{userId}")
    public ResponseEntity confirmRoleRequest(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @PathVariable("role") Role role,
            @PathVariable("userId") Long userId) {
        User adminUser = new User(userInfoDto);
        List<Role> roles = adminUser.getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        LocaleContextHolder.setLocale(adminUser.getLocale());
        User user = userService.getUser(userId);
        libraryService.confirmRoleRequest(user, role);
        AdminPanelPageDto adminPanelPageDto = libraryService.getAdminPanelPage(DEFAULT_PAGE_VALUE, DEFAULT_SIZE_VALUE);
        return ResponseEntity.ok(adminPanelPageDto);
    }
}
