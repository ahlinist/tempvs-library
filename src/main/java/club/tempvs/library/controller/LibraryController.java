package club.tempvs.library.controller;

import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Validated
public class LibraryController {

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final int DEFAULT_PAGE_VALUE = 0;
    private static final int DEFAULT_SIZE_VALUE = 40;

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
            @RequestParam(PAGE_PARAM) int page,
            @Max(40) @RequestParam(SIZE_PARAM) int size) {
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
