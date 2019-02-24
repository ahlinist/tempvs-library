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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
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

    @GetMapping
    public WelcomePageDto getWelcomePage(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto) {
        User user = new User(userInfoDto);
        return libraryService.getWelcomePage(user);
    }

    @PostMapping("/role/{role}")
    public WelcomePageDto requestRole(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @PathVariable("role") Role role) {
        User user = new User(userInfoDto);
        return  libraryService.requestRole(user, role);
    }


    @DeleteMapping("/role/{role}")
    public WelcomePageDto cancelRoleRequest(
            @RequestHeader(USER_INFO_HEADER) UserInfoDto userInfoDto,
            @PathVariable("role") Role role) {
        User user = new User(userInfoDto);
        libraryService.deleteRoleRequest(user, role);
        return libraryService.getWelcomePage(user);
    }

    @GetMapping("/admin")
    public AdminPanelPageDto getAdminPanelPage(
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
        return libraryService.getAdminPanelPage(page, size);
    }

    @DeleteMapping("/{role}/{userId}")
    public AdminPanelPageDto denyRoleRequest(
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
        return libraryService.getAdminPanelPage(DEFAULT_PAGE_VALUE, DEFAULT_SIZE_VALUE);
    }

    @PostMapping("/{role}/{userId}")
    public AdminPanelPageDto confirmRoleRequest(
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
        return libraryService.getAdminPanelPage(DEFAULT_PAGE_VALUE, DEFAULT_SIZE_VALUE);
    }
}
