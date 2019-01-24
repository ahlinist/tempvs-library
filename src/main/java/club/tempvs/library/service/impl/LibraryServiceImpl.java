package club.tempvs.library.service.impl;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.model.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.RoleRequestService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private static final String GREETING_KEY = "greeting";
    private static final String REQUEST_KEY = "request";
    private static final String CANCEL_KEY = "cancel";
    private static final String REQUESTED_ROLE_KEY = "requestedRole";
    private static final String ADMIN_PANEL_BUTTON = "library.admin.panel.button.text";

    private static final Map<Role, Map<String, String>> ROLE_ACTIONS = ImmutableMap.of(
            Role.ROLE_USER, ImmutableMap.of(
                    GREETING_KEY, "library.user.welcome.message",
                    REQUEST_KEY, "library.request.contributor.button",
                    CANCEL_KEY, "library.cancel.contributor.button",
                    REQUESTED_ROLE_KEY, Role.ROLE_CONTRIBUTOR.toString()
            ),
            Role.ROLE_CONTRIBUTOR, ImmutableMap.of(
                    GREETING_KEY, "library.contributor.welcome.message",
                    REQUEST_KEY, "library.request.scribe.button",
                    CANCEL_KEY, "library.cancel.scribe.button",
                    REQUESTED_ROLE_KEY, Role.ROLE_SCRIBE.toString()
            ),
            Role.ROLE_SCRIBE, ImmutableMap.of(
                    GREETING_KEY, "library.scribe.welcome.message",
                    REQUEST_KEY, "library.request.archivarius.button",
                    CANCEL_KEY, "library.cancel.archivarius.button",
                    REQUESTED_ROLE_KEY, Role.ROLE_ARCHIVARIUS.toString()
            ),
            Role.ROLE_ARCHIVARIUS, ImmutableMap.of(
                    GREETING_KEY, "library.archivarius.welcome.message"
            ),
            Role.ROLE_ADMIN, ImmutableMap.of(
                    GREETING_KEY, "library.archivarius.welcome.message"
            )
    );

    private final MessageSource messageSource;
    private final RoleRequestService roleRequestService;


    @Override
    public WelcomePageDto getWelcomePage(User user) {
        return getWelcomePage(user, null);
    }

    @Override
    public WelcomePageDto requestRole(User user, Role role) {
        RoleRequest roleRequest = roleRequestService.createRoleRequest(user, role);
        return getWelcomePage(user, roleRequest);
    }

    @Override
    public WelcomePageDto cancelRoleRequest(User user, Role role) {
        roleRequestService.findRoleRequest(user, role)
                .ifPresent(roleRequestService::deleteRoleRequest);
        return getWelcomePage(user, null);
    }

    private WelcomePageDto getWelcomePage(User user, RoleRequest roleRequest) {
        List<Role> roles = user.getRoles();

        if (roles.contains(Role.ROLE_ADMIN) || roles.contains(Role.ROLE_ARCHIVARIUS)) {
            return getArchivariusWelcomePage(user);
        } else if (roles.contains(Role.ROLE_SCRIBE)) {
            return getUserWelcomePage(user, Role.ROLE_SCRIBE, roleRequest);
        } else if (roles.contains(Role.ROLE_CONTRIBUTOR)) {
            return getUserWelcomePage(user, Role.ROLE_CONTRIBUTOR, roleRequest);
        } else {
            return getUserWelcomePage(user, Role.ROLE_USER, roleRequest);
        }
    }

    private WelcomePageDto getArchivariusWelcomePage(User user) {
        Map<String, String> actionMap = ROLE_ACTIONS.get(Role.ROLE_ARCHIVARIUS);
        Locale locale = user.getLocale();
        String greetingKey = actionMap.get(GREETING_KEY);
        String greeting = messageSource.getMessage(greetingKey, null, greetingKey, locale);
        String buttonText = messageSource.getMessage(ADMIN_PANEL_BUTTON, null, ADMIN_PANEL_BUTTON, locale);

        return new WelcomePageDto(greeting, buttonText, true, false, null);
    }

    private WelcomePageDto getUserWelcomePage(User user, Role role, RoleRequest roleRequest) {
        Map<String, String> actionMap = ROLE_ACTIONS.get(role);
        Locale locale = user.getLocale();
        Role roleToRequest = Role.valueOf(actionMap.get(REQUESTED_ROLE_KEY));

        boolean isRoleRequestAvailable = Optional.ofNullable(roleRequest)
                .map(Objects::isNull)
                .orElseGet(() -> !roleRequestService.findRoleRequest(user, roleToRequest).isPresent());

        String greetingKey = actionMap.get(GREETING_KEY);
        String buttonTextKey = actionMap.get(isRoleRequestAvailable ? REQUEST_KEY : CANCEL_KEY);
        String greeting = messageSource.getMessage(greetingKey, null, greetingKey, locale);
        String buttonText = messageSource.getMessage(buttonTextKey, null, buttonTextKey, locale);

        return new WelcomePageDto(greeting, buttonText, false, isRoleRequestAvailable, actionMap.get(REQUESTED_ROLE_KEY));
    }
}
