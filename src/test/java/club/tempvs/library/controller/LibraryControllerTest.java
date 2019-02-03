package club.tempvs.library.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import club.tempvs.library.api.ForbiddenException;
import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.util.AuthHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerTest {

    private static final String TOKEN = "token";

    private LibraryController libraryController;

    @Mock
    private AuthHelper authHelper;
    @Mock
    private LibraryService libraryService;
    @Mock
    private WelcomePageDto welcomePageDto;
    @Mock
    private AdminPanelPageDto adminPanelPageDto;

    @Before
    public void setup() {
        libraryController = new LibraryController(authHelper, libraryService);
    }

    @Test
    public void testPing() {
        String pong = "pong!";

        String result = libraryController.getPong();

        assertEquals("'pong!' is returned", pong, result);
    }

    @Test
    public void testGetWelcomePage() {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = new User(userInfoDto);

        when(libraryService.getWelcomePage(user)).thenReturn(welcomePageDto);

        ResponseEntity result = libraryController.getWelcomePage(userInfoDto, TOKEN);

        verify(libraryService).getWelcomePage(user);
        verifyNoMoreInteractions(libraryService);

        WelcomePageDto resultDto = (WelcomePageDto) result.getBody();
        assertEquals("The result is a role request", welcomePageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }

    @Test
    public void testRequestRole() {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = new User(userInfoDto);
        Role role = Role.ROLE_CONTRIBUTOR;

        when(libraryService.requestRole(user, role)).thenReturn(welcomePageDto);

        ResponseEntity result = libraryController.requestRole(userInfoDto, TOKEN, role);

        verify(libraryService).requestRole(user, role);
        verifyNoMoreInteractions(libraryService);

        WelcomePageDto resultDto = (WelcomePageDto) result.getBody();
        assertEquals("The result is a role request", welcomePageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }

    @Test
    public void testCancelRoleRequest() {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = new User(userInfoDto);
        Role role = Role.ROLE_SCRIBE;

        when(libraryService.getWelcomePage(user)).thenReturn(welcomePageDto);

        ResponseEntity result = libraryController.cancelRoleRequest(userInfoDto, TOKEN, role);

        verify(libraryService).deleteRoleRequest(user, role);
        verify(libraryService).getWelcomePage(user);
        verifyNoMoreInteractions(libraryService);

        WelcomePageDto resultDto = (WelcomePageDto) result.getBody();
        assertEquals("The result is a role request", welcomePageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }

    @Test
    public void testGetAdminPanelPage() {
        int page = 1;
        int size = 40;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_ARCHIVARIUS"));

        when(libraryService.getAdminPanelPage(page, size)).thenReturn(adminPanelPageDto);

        ResponseEntity result = libraryController.getAdminPanelPage(userInfoDto, TOKEN, page, size);

        verify(libraryService).getAdminPanelPage(page, size);
        verifyNoMoreInteractions(libraryService);

        AdminPanelPageDto resultDto = (AdminPanelPageDto) result.getBody();
        assertEquals("The result is a role request", adminPanelPageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAdminPanelPageForInvalidPageSize() {
        int page = 1;
        int size = 41;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setRoles(Arrays.asList("ROLE_ARCHIVARIUS"));

        libraryController.getAdminPanelPage(userInfoDto, TOKEN, page, size);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetAdminPanelPageForInsufficientAuthorities() {
        int page = 1;
        int size = 40;
        UserInfoDto userInfoDto = new UserInfoDto();

        libraryController.getAdminPanelPage(userInfoDto, TOKEN, page, size);
    }
}
