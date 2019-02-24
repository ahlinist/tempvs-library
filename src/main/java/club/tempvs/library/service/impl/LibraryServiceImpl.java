package club.tempvs.library.service.impl;

import static java.util.stream.Collectors.toList;

import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.dto.RoleRequestDto;
import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;
import club.tempvs.library.service.LibraryService;
import club.tempvs.library.service.RoleRequestService;
import club.tempvs.library.holder.UserHolder;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final UserHolder userHolder;

    @Override
    public WelcomePageDto getWelcomePage() {
        User user = userHolder.getUser();
        return getWelcomePage(user, null);
    }

    @Override
    public WelcomePageDto requestRole(Role role) {
        User user = userHolder.getUser();
        RoleRequest roleRequest = roleRequestService.createRoleRequest(user, role);
        return getWelcomePage(user, roleRequest);
    }

    @Override
    public AdminPanelPageDto getAdminPanelPage(int page, int size) {
        User user = userHolder.getUser();
        List<Role> roles = user.getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        List<RoleRequest> roleRequests = roleRequestService.getRoleRequests(page, size);

        List<RoleRequestDto> roleRequestDtos = roleRequests.stream()
                .map(roleRequest -> {
                    Role role = roleRequest.getRole();
                    String roleKey = role.getKey();
                    String roleLabel = messageSource
                            .getMessage(roleKey, null, roleKey, LocaleContextHolder.getLocale());
                    return new RoleRequestDto(roleRequest.getUser(), role, roleLabel);
                }).collect(toList());
        return new AdminPanelPageDto(roleRequestDtos);
    }

    @Override
    public void cancelRoleRequest(Role role) {
        User user = userHolder.getUser();
        roleRequestService.findRoleRequest(user, role)
                .ifPresent(roleRequestService::deleteRoleRequest);
    }

    @Override
    public void denyRoleRequest(User user, Role role) {
        List<Role> roles = userHolder.getUser().getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        roleRequestService.findRoleRequest(user, role)
                .ifPresent(roleRequestService::deleteRoleRequest);
    }

    @Override
    public void confirmRoleRequest(User user, Role role) {
        List<Role> roles = userHolder.getUser().getRoles();

        if (!roles.contains(Role.ROLE_ADMIN) && !roles.contains(Role.ROLE_ARCHIVARIUS)) {
            throw new ForbiddenException("Access denied. Archivarius or admin role is required.");
        }

        roleRequestService.findRoleRequest(user, role)
                .ifPresent(roleRequestService::confirmRoleRequest);
    }

    private WelcomePageDto getWelcomePage(User user, RoleRequest roleRequest) {
        List<Role> roles = user.getRoles();

        if (roles.contains(Role.ROLE_ADMIN) || roles.contains(Role.ROLE_ARCHIVARIUS)) {
            return getArchivariusWelcomePage();
        } else if (roles.contains(Role.ROLE_SCRIBE)) {
            return getUserWelcomePage(user, Role.ROLE_SCRIBE, roleRequest);
        } else if (roles.contains(Role.ROLE_CONTRIBUTOR)) {
            return getUserWelcomePage(user, Role.ROLE_CONTRIBUTOR, roleRequest);
        } else {
            return getUserWelcomePage(user, Role.ROLE_USER, roleRequest);
        }
    }

    private WelcomePageDto getArchivariusWelcomePage() {
        Map<String, String> actionMap = ROLE_ACTIONS.get(Role.ROLE_ARCHIVARIUS);
        Locale locale = LocaleContextHolder.getLocale();
        String greetingKey = actionMap.get(GREETING_KEY);
        String greeting = messageSource.getMessage(greetingKey, null, greetingKey, locale);
        String buttonText = messageSource.getMessage(ADMIN_PANEL_BUTTON, null, ADMIN_PANEL_BUTTON, locale);

        return new WelcomePageDto(greeting, buttonText, true, false, null);
    }

    private WelcomePageDto getUserWelcomePage(User user, Role role, RoleRequest roleRequest) {
        Map<String, String> actionMap = ROLE_ACTIONS.get(role);
        Locale locale = LocaleContextHolder.getLocale();
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
