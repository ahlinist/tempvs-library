package club.tempvs.library.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.model.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.impl.LibraryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryServiceTest {

    private static final String USER_GREETING = "library.user.welcome.message";
    private static final String CONTRIBUTOR_GREETING = "library.contributor.welcome.message";
    private static final String SCRIBE_GREETING = "library.scribe.welcome.message";
    private static final String ARCHIVARIUS_GREETING = "library.archivarius.welcome.message";
    private static final String ADMIN_PANEL_BUTTON = "library.admin.panel.button.text";
    private static final String REQUEST_CONTRIBUTOR_BUTTON = "library.request.contributor.button";
    private static final String REQUEST_SCRIBE_BUTTON = "library.request.scribe.button";
    private static final String REQUEST_ARCHIVARIUS_BUTTON = "library.request.contributor.button";
    private static final String CANCEL_CONTRIBUTOR_BUTTON = "library.cancel.contributor.button";
    private static final String CANCEL_SCRIBE_BUTTON = "library.cancel.scribe.button";
    private static final String CANCEL_ARCHIVARIUS_BUTTON = "library.cancel.archivarius.button";

    private LibraryService libraryService;

    @Mock
    private User user;

    @Mock
    private MessageSource messageSource;

    @Mock
    private RoleRequest roleRequest;

    @Mock
    private RoleRequestService roleRequestService;

    @Before
    public void setup() {
        libraryService = new LibraryServiceImpl(messageSource, roleRequestService);
    }

    @Test
    public void testGetWelcomePageForAdmin() {
        String greeting = "archivarius greeting";
        String adminPanelButtonText = "admin panel";

        List<Role> roles = Arrays.asList(Role.ROLE_ADMIN);

        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(ARCHIVARIUS_GREETING, null, ARCHIVARIUS_GREETING, Locale.ENGLISH))
                .thenReturn(greeting);
        when(messageSource.getMessage(ADMIN_PANEL_BUTTON, null, ADMIN_PANEL_BUTTON, Locale.ENGLISH))
                .thenReturn(adminPanelButtonText);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        assertEquals("Greeting text matches Archivarius", greeting, result.getGreeting());
        assertEquals("Button text matches 'admin panel'", adminPanelButtonText, result.getButtonText());
        assertEquals("Admin panel is available", true, result.isAdminPanelAvailable());
        assertEquals("No role request option is available", false, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForArchivarius() {
        String greeting = "archivarius greeting";
        String adminPanelButtonText = "admin panel";

        List<Role> roles = Arrays.asList(Role.ROLE_ARCHIVARIUS);

        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(ARCHIVARIUS_GREETING, null, ARCHIVARIUS_GREETING, Locale.ENGLISH))
                .thenReturn(greeting);
        when(messageSource.getMessage(ADMIN_PANEL_BUTTON, null, ADMIN_PANEL_BUTTON, Locale.ENGLISH))
                .thenReturn(adminPanelButtonText);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        assertEquals("Greeting text matches Archivarius", greeting, result.getGreeting());
        assertEquals("Button text matches 'admin panel'", adminPanelButtonText, result.getButtonText());
        assertEquals("Admin panel is available", true, result.isAdminPanelAvailable());
        assertEquals("No role request option is available", false, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForOrdinaryUser() {
        String greeting = "user greeting";
        String requestContributorButton = "request contributor";
        Long userId = 1L;

        List<Role> roles = new ArrayList<>();

        when(user.getId()).thenReturn(userId);
        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(USER_GREETING, null, USER_GREETING, Locale.ENGLISH)).thenReturn(greeting);
        when(roleRequestService.findRoleRequest(userId, Role.ROLE_CONTRIBUTOR)).thenReturn(Optional.empty());
        when(messageSource.getMessage(REQUEST_CONTRIBUTOR_BUTTON, null, REQUEST_CONTRIBUTOR_BUTTON, Locale.ENGLISH))
                .thenReturn(requestContributorButton);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        verify(roleRequestService).findRoleRequest(userId, Role.ROLE_CONTRIBUTOR);
        verifyNoMoreInteractions(roleRequestService);

        assertEquals("Greeting text matches user", greeting, result.getGreeting());
        assertEquals("Button text matches 'contributor request'", requestContributorButton, result.getButtonText());
        assertEquals("Admin panel is not available", false, result.isAdminPanelAvailable());
        assertEquals("Role field corresponds contributor", Role.ROLE_CONTRIBUTOR.toString(), result.getRole());
        assertEquals("New role request is available", true, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForOrdinaryUserHavingContributorRequested() {
        String greeting = "user greeting";
        String cancelContributorButton = "cancel contributor";
        Long userId = 1L;

        List<Role> roles = new ArrayList<>();

        when(user.getId()).thenReturn(userId);
        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(USER_GREETING, null, USER_GREETING, Locale.ENGLISH)).thenReturn(greeting);
        when(roleRequestService.findRoleRequest(userId, Role.ROLE_CONTRIBUTOR)).thenReturn(Optional.of(roleRequest));
        when(messageSource.getMessage(CANCEL_CONTRIBUTOR_BUTTON, null, CANCEL_CONTRIBUTOR_BUTTON, Locale.ENGLISH))
                .thenReturn(cancelContributorButton);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        verify(roleRequestService).findRoleRequest(userId, Role.ROLE_CONTRIBUTOR);
        verifyNoMoreInteractions(roleRequestService);

        assertEquals("Greeting text matches user", greeting, result.getGreeting());
        assertEquals("Button text matches 'contributor request'", cancelContributorButton, result.getButtonText());
        assertEquals("Admin panel is not available", false, result.isAdminPanelAvailable());
        assertEquals("Role field corresponds contributor", Role.ROLE_CONTRIBUTOR.toString(), result.getRole());
        assertEquals("Role cancellation option is available", false, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForContributor() {
        String greeting = "contributor greeting";
        String requestScribeButton = "request scribe";
        Long userId = 1L;

        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(user.getId()).thenReturn(userId);
        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(CONTRIBUTOR_GREETING, null, CONTRIBUTOR_GREETING, Locale.ENGLISH)).thenReturn(greeting);
        when(roleRequestService.findRoleRequest(userId, Role.ROLE_SCRIBE)).thenReturn(Optional.empty());
        when(messageSource.getMessage(REQUEST_SCRIBE_BUTTON, null, REQUEST_SCRIBE_BUTTON, Locale.ENGLISH))
                .thenReturn(requestScribeButton);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        verify(roleRequestService).findRoleRequest(userId, Role.ROLE_SCRIBE);
        verifyNoMoreInteractions(roleRequestService);

        assertEquals("Greeting text matches contributor", greeting, result.getGreeting());
        assertEquals("Button text matches 'scribe request'", requestScribeButton, result.getButtonText());
        assertEquals("Admin panel is not available", false, result.isAdminPanelAvailable());
        assertEquals("Role field corresponds scribe", Role.ROLE_SCRIBE.toString(), result.getRole());
        assertEquals("New role request is available", true, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForContributorHavingScribeRequested() {
        String greeting = "contributor greeting";
        String cancelScribeButton = "cancel scribe";
        Long userId = 1L;

        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(user.getId()).thenReturn(userId);
        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(CONTRIBUTOR_GREETING, null, CONTRIBUTOR_GREETING, Locale.ENGLISH)).thenReturn(greeting);
        when(roleRequestService.findRoleRequest(userId, Role.ROLE_SCRIBE)).thenReturn(Optional.of(roleRequest));
        when(messageSource.getMessage(CANCEL_SCRIBE_BUTTON, null, CANCEL_SCRIBE_BUTTON, Locale.ENGLISH))
                .thenReturn(cancelScribeButton);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        verify(roleRequestService).findRoleRequest(userId, Role.ROLE_SCRIBE);
        verifyNoMoreInteractions(roleRequestService);

        assertEquals("Greeting text matches user", greeting, result.getGreeting());
        assertEquals("Button text matches 'contributor request'", cancelScribeButton, result.getButtonText());
        assertEquals("Admin panel is not available", false, result.isAdminPanelAvailable());
        assertEquals("Role field corresponds contributor", Role.ROLE_SCRIBE.toString(), result.getRole());
        assertEquals("Role cancellation option is available", false, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForScribe() {
        String greeting = "scribe greeting";
        String requestArchivariusButton = "request archivarius";
        Long userId = 1L;

        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(user.getId()).thenReturn(userId);
        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(SCRIBE_GREETING, null, SCRIBE_GREETING, Locale.ENGLISH)).thenReturn(greeting);
        when(roleRequestService.findRoleRequest(userId, Role.ROLE_ARCHIVARIUS)).thenReturn(Optional.empty());
        when(messageSource.getMessage(REQUEST_ARCHIVARIUS_BUTTON, null, REQUEST_ARCHIVARIUS_BUTTON, Locale.ENGLISH))
                .thenReturn(requestArchivariusButton);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        verify(roleRequestService).findRoleRequest(userId, Role.ROLE_ARCHIVARIUS);
        verifyNoMoreInteractions(roleRequestService);

        assertEquals("Greeting text matches contributor", greeting, result.getGreeting());
        assertEquals("Button text matches 'archivarius request'", requestArchivariusButton, result.getButtonText());
        assertEquals("Admin panel is not available", false, result.isAdminPanelAvailable());
        assertEquals("Role field corresponds archivarius", Role.ROLE_ARCHIVARIUS.toString(), result.getRole());
        assertEquals("New role request is available", true, result.isRoleRequest());
    }

    @Test
    public void testGetWelcomePageForContributorHavingAchivariusRequested() {
        String greeting = "scribe greeting";
        String cancelArchivariusButton = "cancel archivarius";
        Long userId = 1L;

        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(user.getId()).thenReturn(userId);
        when(user.getRoles()).thenReturn(roles);
        when(user.getLocale()).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(SCRIBE_GREETING, null, SCRIBE_GREETING, Locale.ENGLISH)).thenReturn(greeting);
        when(roleRequestService.findRoleRequest(userId, Role.ROLE_ARCHIVARIUS)).thenReturn(Optional.of(roleRequest));
        when(messageSource.getMessage(CANCEL_ARCHIVARIUS_BUTTON, null, CANCEL_ARCHIVARIUS_BUTTON, Locale.ENGLISH))
                .thenReturn(cancelArchivariusButton);

        WelcomePageDto result = libraryService.getWelcomePage(user);

        verify(roleRequestService).findRoleRequest(userId, Role.ROLE_ARCHIVARIUS);
        verifyNoMoreInteractions(roleRequestService);

        assertEquals("Greeting text matches user", greeting, result.getGreeting());
        assertEquals("Button text matches 'contributor request'", cancelArchivariusButton, result.getButtonText());
        assertEquals("Admin panel is not available", false, result.isAdminPanelAvailable());
        assertEquals("Role field corresponds contributor", Role.ROLE_ARCHIVARIUS.toString(), result.getRole());
        assertEquals("Role cancellation option is available", false, result.isRoleRequest());
    }
}
