package club.tempvs.library.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.library.dao.RoleRequestRepository;
import club.tempvs.library.model.Role;
import club.tempvs.library.domain.RoleRequest;
import club.tempvs.library.domain.User;
import club.tempvs.library.service.impl.RoleRequestServiceImpl;
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
    private User user;

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

        when(user.getId()).thenReturn(userId);
        when(roleRequestRepository.findByUserIdAndRole(userId, role)).thenReturn(Optional.of(roleRequest));

        Optional<RoleRequest> result = roleRequestService.findRoleRequest(user, role);

        verify(roleRequestRepository).findByUserIdAndRole(userId, role);
        verifyNoMoreInteractions(roleRequestRepository);

        assertEquals("Optional of roleRequest is returned", Optional.of(roleRequest), result);
    }

    @Test
    public void testCreateRoleRequest() {
        Long userId = 1L;
        Role role = Role.ROLE_SCRIBE;
        User user = new User();
        user.setId(userId);
        RoleRequest roleRequest = new RoleRequest(user, role);

        when(roleRequestRepository.findByUserIdAndRole(userId, role)).thenReturn(Optional.empty());
        when(roleRequestRepository.save(roleRequest)).thenReturn(roleRequest);

        RoleRequest result = roleRequestService.createRoleRequest(user, role);

        verify(roleRequestRepository).findByUserIdAndRole(userId, role);
        verify(roleRequestRepository).save(roleRequest);
        verifyNoMoreInteractions(roleRequestRepository);

        assertEquals("RoleRequest is created for scribe role", role, result.getRole());
        assertEquals("RoleRequest is created for user with id " + userId, user, result.getUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRoleRequestForExistentEntry() {
        Long userId = 1L;
        Role role = Role.ROLE_SCRIBE;
        RoleRequest roleRequest = new RoleRequest(user, role);

        when(user.getId()).thenReturn(userId);
        when(roleRequestRepository.findByUserIdAndRole(userId, role)).thenReturn(Optional.of(roleRequest));

        roleRequestService.createRoleRequest(user, role);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateRoleRequestForWrongRole() {
        Role role = Role.ROLE_ADMIN;

        roleRequestService.createRoleRequest(user, role);
    }

    @Test
    public void testDeleteRoleRequest() {
        Long id = 1L;

        when(roleRequest.getId()).thenReturn(id);

        roleRequestService.deleteRoleRequest(roleRequest);

        verify(roleRequestRepository).deleteById(id);
        verifyNoMoreInteractions(roleRequestRepository);
    }
}
