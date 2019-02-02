package club.tempvs.library.service;

import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;

public interface LibraryService {

    WelcomePageDto getWelcomePage(User user);

    WelcomePageDto requestRole(User user, Role role);

    WelcomePageDto cancelRoleRequest(User user, Role role);

    AdminPanelPageDto getAdminPanelPage(User user, int page, int size);
}
