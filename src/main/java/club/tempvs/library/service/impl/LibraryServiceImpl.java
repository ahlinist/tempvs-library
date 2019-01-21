package club.tempvs.library.service.impl;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.model.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.RoleRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private static final String USER_GREETING = "library.user.welcome.message";
    private static final String CONTRIBUTOR_GREETING = "library.contributor.welcome.message";
    private static final String SCRIBE_GREETING = "library.scribe.welcome.message";
    private static final String ARCHIVARIUS_GREETING = "library.archivarius.welcome.message";
    private static final String ADMIN_PANEL_BUTTON = "library.admin.panel.button.text";
    private static final String REQUEST_CONTRIBUTOR_BUTTON = "library.request.contributor.button";
    private static final String REQUEST_SCRIBE_BUTTON = "library.request.scribe.button";
    private static final String REQUEST_ARCHIVARIUS_BUTTON = "library.request.archivarius.button";
    private static final String CANCEL_CONTRIBUTOR_BUTTON = "library.cancel.contributor.button";
    private static final String CANCEL_SCRIBE_BUTTON = "library.cancel.scribe.button";
    private static final String CANCEL_ARCHIVARIUS_BUTTON = "library.cancel.archivarius.button";

    private final MessageSource messageSource;
    private final RoleRequestService roleRequestService;

    @Override
    public WelcomePageDto getWelcomePage(User user) {
        List<Role> roles = user.getRoles();

        if (roles.contains(Role.ROLE_ADMIN) || roles.contains(Role.ROLE_ARCHIVARIUS)) {
            return getArchivariusWelcomePage(user);
        } else if (roles.contains(Role.ROLE_SCRIBE)) {
            return getUserWelcomePage(user, Role.ROLE_ARCHIVARIUS,
                    SCRIBE_GREETING, REQUEST_ARCHIVARIUS_BUTTON, CANCEL_ARCHIVARIUS_BUTTON);
        } else if (roles.contains(Role.ROLE_CONTRIBUTOR)) {
            return getUserWelcomePage(user, Role.ROLE_SCRIBE,
                    CONTRIBUTOR_GREETING, REQUEST_SCRIBE_BUTTON, CANCEL_SCRIBE_BUTTON);
        } else {
            return getUserWelcomePage(user, Role.ROLE_CONTRIBUTOR,
                    USER_GREETING, REQUEST_CONTRIBUTOR_BUTTON, CANCEL_CONTRIBUTOR_BUTTON);
        }
    }

    private WelcomePageDto getArchivariusWelcomePage(User user) {
        Locale locale = user.getLocale();
        WelcomePageDto welcomePageDto = new WelcomePageDto();
        String greeting = messageSource.getMessage(ARCHIVARIUS_GREETING, null, ARCHIVARIUS_GREETING, locale);
        String buttonText = messageSource.getMessage(ADMIN_PANEL_BUTTON, null, ADMIN_PANEL_BUTTON, locale);
        welcomePageDto.setGreeting(greeting);
        welcomePageDto.setAdminPanelAvailable(true);
        welcomePageDto.setButtonText(buttonText);
        return welcomePageDto;
    }

    private WelcomePageDto getUserWelcomePage(User user, Role role, String greetingKey, String requestKey, String cancelKey) {
        Long userId = user.getId();
        Locale locale = user.getLocale();
        WelcomePageDto welcomePageDto = new WelcomePageDto();
        String greeting = messageSource.getMessage(greetingKey, null, greetingKey, locale);
        welcomePageDto.setGreeting(greeting);
        Optional<RoleRequest> request = roleRequestService.findRoleRequest(userId, role);
        String buttonText;

        if (request.isPresent()) {
            buttonText = messageSource
                    .getMessage(cancelKey, null, cancelKey, locale);
            welcomePageDto.setRoleRequest(false);
        } else {
            buttonText = messageSource
                    .getMessage(requestKey, null, requestKey, locale);
            welcomePageDto.setRoleRequest(true);
        }

        welcomePageDto.setButtonText(buttonText);
        welcomePageDto.setAdminPanelAvailable(false);
        welcomePageDto.setRole(role.toString());
        return welcomePageDto;
    }
}
