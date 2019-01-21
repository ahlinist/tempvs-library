package club.tempvs.library.service.impl;

import club.tempvs.library.dao.RoleRequestRepository;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.model.User;
import club.tempvs.library.service.RoleRequestService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRequestServiceImpl implements RoleRequestService {

    private final RoleRequestRepository roleRequestRepository;

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public Optional<RoleRequest> findRoleRequest(Long userId, Role role) {
        return roleRequestRepository.findByUserIdAndRole(userId, role);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public RoleRequest createRoleRequest(User user, Role role) {
        List<Role> allowedRoles = Arrays.asList(Role.ROLE_CONTRIBUTOR, Role.ROLE_SCRIBE, Role.ROLE_ARCHIVARIUS);

        if (!allowedRoles.contains(role)) {
            throw new UnsupportedOperationException("Only CONTRIBUTOR, SCRIBE and ARCHIVARIUS roles can be requested");
        }

        Long userId = user.getId();

        if (findRoleRequest(userId, role).isPresent()) {
            throw new IllegalArgumentException("User with id " + userId + " has already requested " + role.toString() + " role");
        }

        RoleRequest roleRequest = new RoleRequest(userId, role);
        return roleRequestRepository.save(roleRequest);
    }
}
