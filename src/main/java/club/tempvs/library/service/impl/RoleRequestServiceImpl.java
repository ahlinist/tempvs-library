package club.tempvs.library.service.impl;

import club.tempvs.library.dao.RoleRequestRepository;
import club.tempvs.library.dto.UserRolesDto;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.domain.User;
import club.tempvs.library.service.RoleRequestService;
import club.tempvs.library.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRequestServiceImpl implements RoleRequestService {

    private final static List<Role> ALLOWED_ROLES = Arrays.asList(
            Role.ROLE_CONTRIBUTOR, Role.ROLE_SCRIBE, Role.ROLE_ARCHIVARIUS);

    private final RoleRequestRepository roleRequestRepository;
    private final UserService userService;

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public Optional<RoleRequest> findRoleRequest(User user, Role role) {
        return roleRequestRepository.findByUserAndRole(user, role);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public RoleRequest createRoleRequest(User user, Role role) {
        if (!ALLOWED_ROLES.contains(role)) {
            throw new UnsupportedOperationException("Only CONTRIBUTOR, SCRIBE and ARCHIVARIUS roles can be requested");
        }

        if (findRoleRequest(user, role).isPresent()) {
            throw new IllegalArgumentException(
                    "User with id " + user.getId() + " has already requested " + role.toString() + " role");
        }

        RoleRequest roleRequest = new RoleRequest(user, role);
        return roleRequestRepository.save(roleRequest);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public void deleteRoleRequest(RoleRequest roleRequest) {
        Long id = roleRequest.getId();
        roleRequestRepository.deleteById(id);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public List<RoleRequest> getRoleRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");
        Page<RoleRequest> roleRequestPage = roleRequestRepository.findAll(pageable);
        return roleRequestPage.getContent();
    }

    @Override
    public void confirmRoleRequest(RoleRequest roleRequest) {
        User user = roleRequest.getUser();
        Long userId = user.getId();
        Role role = roleRequest.getRole();
        List<String> roles = Arrays.asList(role.toString());
        UserRolesDto userRolesDto = new UserRolesDto(userId, roles);
        userService.updateUserRoles(userRolesDto);
        deleteRoleRequest(roleRequest);
    }
}
