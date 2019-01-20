package club.tempvs.library.service.impl;

import club.tempvs.library.RoleRequestRepository;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.service.RoleRequestService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
