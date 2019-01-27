package club.tempvs.library.service;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.domain.User;

import java.util.Optional;

public interface RoleRequestService {

    Optional<RoleRequest> findRoleRequest(User user, Role role);
    RoleRequest createRoleRequest(User user, Role role);
    void deleteRoleRequest(RoleRequest roleRequest);
}
