package club.tempvs.library.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerTest {

    private LibraryController libraryController;

    @Mock
    private LibraryService libraryService;
    @Mock
    private WelcomePageDto welcomePageDto;
    @Mock
    private AdminPanelPageDto adminPanelPageDto;
    @Mock
    private UserService userService;

    @Before
    public void setup() {
        libraryController = new LibraryController(libraryService, userService);
    }

    @Test
    public void testGetWelcomePage() {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = new User(userInfoDto);

        when(libraryService.getWelcomePage(user)).thenReturn(welcomePageDto);

        WelcomePageDto result = libraryController.getWelcomePage(userInfoDto);

        verify(libraryService).getWelcomePage(user);
        verifyNoMoreInteractions(libraryService);

        assertEquals("The result is a role request", welcomePageDto, result);
    }

    @Test
    public void testRequestRole() {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = new User(userInfoDto);
        Role role = Role.ROLE_CONTRIBUTOR;

        when(libraryService.requestRole(user, role)).thenReturn(welcomePageDto);

        WelcomePageDto result = libraryController.requestRole(userInfoDto, role);

        verify(libraryService).requestRole(user, role);
        verifyNoMoreInteractions(libraryService);

        assertEquals("The result is a role request", welcomePageDto, result);
    }

    @Test
    public void testCancelRoleRequest() {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = new User(userInfoDto);
        Role role = Role.ROLE_SCRIBE;

        when(libraryService.getWelcomePage(user)).thenReturn(welcomePageDto);

        WelcomePageDto result = libraryController.cancelRoleRequest(userInfoDto, role);

        verify(libraryService).deleteRoleRequest(user, role);
        verify(libraryService).getWelcomePage(user);
        verifyNoMoreInteractions(libraryService);

        assertEquals("The result is a role request", welcomePageDto, result);
    }

    @Test
    public void testGetAdminPanelPage() {
        int page = 1;
        int size = 40;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_ARCHIVARIUS"));

        when(libraryService.getAdminPanelPage(page, size)).thenReturn(adminPanelPageDto);

        AdminPanelPageDto result = libraryController.getAdminPanelPage(userInfoDto, page, size);

        verify(libraryService).getAdminPanelPage(page, size);
        verifyNoMoreInteractions(libraryService);

        assertEquals("The result is a role request", adminPanelPageDto, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAdminPanelPageForInvalidPageSize() {
        int page = 1;
        int size = 41;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_ARCHIVARIUS"));

        libraryController.getAdminPanelPage(userInfoDto, page, size);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetAdminPanelPageForInsufficientAuthorities() {
        int page = 1;
        int size = 40;
        UserInfoDto userInfoDto = new UserInfoDto();

        libraryController.getAdminPanelPage(userInfoDto, page, size);
    }

    @Test
    public void testDenyRoleRequest() {
        int page = 0;
        int size = 40;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_ARCHIVARIUS"));
        Role role = Role.ROLE_SCRIBE;
        Long userId = 1L;
        User user = new User();

        when(userService.getUser(userId)).thenReturn(user);
        when(libraryService.getAdminPanelPage(page, size)).thenReturn(adminPanelPageDto);

        AdminPanelPageDto result = libraryController.denyRoleRequest(userInfoDto, role, userId);

        verify(userService).getUser(userId);
        verify(libraryService).deleteRoleRequest(user, role);
        verify(libraryService).getAdminPanelPage(page, size);
        verifyNoMoreInteractions(userService, libraryService);

        assertEquals("The result is a role request", adminPanelPageDto, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testDenyRoleRequestForInsufficientAuthorities() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_SCRIBE"));
        Role role = Role.ROLE_SCRIBE;
        Long userId = 1L;

        libraryController.denyRoleRequest(userInfoDto, role, userId);
    }

    @Test
    public void testConfirmRoleRequest() {
        int page = 0;
        int size = 40;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_ARCHIVARIUS"));
        Role role = Role.ROLE_SCRIBE;
        Long userId = 1L;
        User user = new User();

        when(userService.getUser(userId)).thenReturn(user);
        when(libraryService.getAdminPanelPage(page, size)).thenReturn(adminPanelPageDto);

        AdminPanelPageDto result = libraryController.confirmRoleRequest(userInfoDto, role, userId);

        verify(userService).getUser(userId);
        verify(libraryService).confirmRoleRequest(user, role);
        verify(libraryService).getAdminPanelPage(page, size);
        verifyNoMoreInteractions(userService, libraryService);

        assertEquals("The result is a role request", adminPanelPageDto, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testConfirmRoleRequestForInsufficientAuthorities() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_SCRIBE"));
        Role role = Role.ROLE_SCRIBE;
        Long userId = 1L;

        libraryController.confirmRoleRequest(userInfoDto, role, userId);
    }
}
