package club.tempvs.library.service;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;

import java.util.Optional;

public interface RoleRequestService {

    Optional<RoleRequest> findRoleRequest(Long userId, Role role);
}
