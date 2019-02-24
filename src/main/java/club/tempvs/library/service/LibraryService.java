package club.tempvs.library.service;

import club.tempvs.library.dto.AdminPanelPageDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.WelcomePageDto;

public interface LibraryService {

    WelcomePageDto getWelcomePage();

    WelcomePageDto requestRole(Role role);

    AdminPanelPageDto getAdminPanelPage(int page, int size);

    void cancelRoleRequest(Role role);

    void denyRoleRequest(User user, Role role);

    void confirmRoleRequest(User user, Role role);
}
