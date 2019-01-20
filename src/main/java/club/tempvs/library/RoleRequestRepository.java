package club.tempvs.library;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {

    Optional<RoleRequest> findByUserIdAndRole(Long userId, Role role);
}
