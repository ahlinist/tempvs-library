package club.tempvs.library.dao;

import club.tempvs.library.domain.User;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {

    Optional<RoleRequest> findByUserAndRole(User user, Role role);
}
