package club.tempvs.library.controller;

import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

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
    public WelcomePageDto getWelcomePage() {
        return libraryService.getWelcomePage();
    }

    @PostMapping("/role/{role}")
    public WelcomePageDto requestRole(@PathVariable("role") Role role) {
        return  libraryService.requestRole(role);
    }

    @DeleteMapping("/role/{role}")
    public WelcomePageDto cancelRoleRequest(@PathVariable("role") Role role) {
        libraryService.cancelRoleRequest(role);
        return libraryService.getWelcomePage();
    }

    @GetMapping("/admin")
    public AdminPanelPageDto getAdminPanelPage(
            @RequestParam(value = PAGE_PARAM, required = false, defaultValue = DEFAULT_PAGE_PARAM) int page,
            @RequestParam(value = SIZE_PARAM, required = false, defaultValue = DEFAULT_SIZE_PARAM) int size) {
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not be larger than " + MAX_PAGE_SIZE + "!");
        }

        return libraryService.getAdminPanelPage(page, size);
    }

    @DeleteMapping("/{role}/{userId}")
    public AdminPanelPageDto denyRoleRequest(@PathVariable("role") Role role, @PathVariable("userId") Long userId) {
        User user = userService.getUser(userId);
        libraryService.denyRoleRequest(user, role);
        return libraryService.getAdminPanelPage(DEFAULT_PAGE_VALUE, DEFAULT_SIZE_VALUE);
    }

    @PostMapping("/{role}/{userId}")
    public AdminPanelPageDto confirmRoleRequest(@PathVariable("role") Role role, @PathVariable("userId") Long userId) {
        User user = userService.getUser(userId);
        libraryService.confirmRoleRequest(user, role);
        return libraryService.getAdminPanelPage(DEFAULT_PAGE_VALUE, DEFAULT_SIZE_VALUE);
    }
}
