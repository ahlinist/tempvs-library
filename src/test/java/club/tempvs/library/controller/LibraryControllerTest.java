package club.tempvs.library.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.RoleRequestService;
import club.tempvs.library.util.AuthHelper;
import club.tempvs.library.util.UserConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerTest {

    private static final String TOKEN = "token";

    private LibraryController libraryController;

    @Mock
    private User user;

    @Mock
    private UserInfoDto userInfoDto;

    @Mock
    private UserConverter userConverter;

    @Mock
    private AuthHelper authHelper;

    @Mock
    private LibraryService libraryService;

    @Mock
    private RoleRequestService roleRequestService;

    @Mock
    private WelcomePageDto welcomePageDto;

    @Mock
    private RoleRequest roleRequest;

    @Before
    public void setup() {
        libraryController = new LibraryController(authHelper, userConverter, libraryService, roleRequestService);
    }

    @Test
    public void testPing() {
        String pong = "pong!";

        String result = libraryController.getPong();

        assertEquals("'pong!' is returned", pong, result);
    }

    @Test
    public void testGetWelcomePage() {

        when(userConverter.convert(userInfoDto)).thenReturn(user);
        when(libraryService.getWelcomePage(user)).thenReturn(welcomePageDto);

        ResponseEntity result = libraryController.getWelcomePage(userInfoDto, TOKEN);

        verify(userConverter).convert(userInfoDto);
        verify(libraryService).getWelcomePage(user);
        verifyNoMoreInteractions(userConverter, libraryService);

        WelcomePageDto resultDto = (WelcomePageDto) result.getBody();
        assertEquals("The result is a role request", welcomePageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }

    @Test
    public void testRequestRole() {
        Role role = Role.ROLE_CONTRIBUTOR;

        when(userConverter.convert(userInfoDto)).thenReturn(user);
        when(libraryService.requestRole(user, role)).thenReturn(welcomePageDto);

        ResponseEntity result = libraryController.requestRole(userInfoDto, TOKEN, role);

        verify(libraryService).requestRole(user, role);
        verifyNoMoreInteractions(libraryService);

        WelcomePageDto resultDto = (WelcomePageDto) result.getBody();
        assertEquals("The result is a role request", welcomePageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }

    @Test
    public void cancelRoleRequest() {
        Role role = Role.ROLE_SCRIBE;

        when(userConverter.convert(userInfoDto)).thenReturn(user);
        when(libraryService.cancelRoleRequest(user, role)).thenReturn(welcomePageDto);

        ResponseEntity result = libraryController.cancelRoleRequest(userInfoDto, TOKEN, role);

        verify(libraryService).cancelRoleRequest(user, role);
        verifyNoMoreInteractions(libraryService);

        WelcomePageDto resultDto = (WelcomePageDto) result.getBody();
        assertEquals("The result is a role request", welcomePageDto, resultDto);
        assertEquals("The result is a role request", 200, result.getStatusCodeValue());
    }
}
