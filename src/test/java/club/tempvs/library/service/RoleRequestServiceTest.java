package club.tempvs.library.service;

import static org.mockito.Mockito.*;

import club.tempvs.library.dao.RoleRequestRepository;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.service.impl.RoleRequestServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RoleRequestServiceTest {

    private RoleRequestService roleRequestService;

    @Mock
    private RoleRequest roleRequest;

    @Mock
    private RoleRequestRepository roleRequestRepository;

    @Before
    public void setup() {
        roleRequestService = new RoleRequestServiceImpl(roleRequestRepository);
    }

    @Test
    public void testFindRoleRequest() {
        Long userId = 1L;
        Role role = Role.ROLE_CONTRIBUTOR;

        when(roleRequestRepository.findByUserIdAndRole(userId, role)).thenReturn(Optional.of(roleRequest));

        Optional<RoleRequest> result = roleRequestService.findRoleRequest(userId, role);

        verify(roleRequestRepository).findByUserIdAndRole(userId, role);
        verifyNoMoreInteractions(roleRequestRepository);

        Assert.assertEquals("Optional of roleRequest is returned", Optional.of(roleRequest), result);
    }
}
