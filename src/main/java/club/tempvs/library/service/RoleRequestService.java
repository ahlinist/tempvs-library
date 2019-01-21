package club.tempvs.library.service;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.model.User;

import java.util.Optional;

public interface RoleRequestService {

    Optional<RoleRequest> findRoleRequest(Long userId, Role role);

    RoleRequest createRoleRequest(User user, Role role);
}
