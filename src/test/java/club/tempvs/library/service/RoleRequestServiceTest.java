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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
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

    @Mock
    private Page<RoleRequest> roleRequestPage;

    @Before
    public void setup() {
        roleRequestService = new RoleRequestServiceImpl(roleRequestRepository);
    }

    @Test
    public void testFindRoleRequest() {
        Long userId = 1L;
        Role role = Role.ROLE_CONTRIBUTOR;

        when(roleRequestRepository.findByUserAndRole(user, role)).thenReturn(Optional.of(roleRequest));

        Optional<RoleRequest> result = roleRequestService.findRoleRequest(user, role);

        verify(roleRequestRepository).findByUserAndRole(user, role);
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

        when(roleRequestRepository.findByUserAndRole(user, role)).thenReturn(Optional.empty());
        when(roleRequestRepository.save(roleRequest)).thenReturn(roleRequest);

        RoleRequest result = roleRequestService.createRoleRequest(user, role);

        verify(roleRequestRepository).findByUserAndRole(user, role);
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
        when(roleRequestRepository.findByUserAndRole(user, role)).thenReturn(Optional.of(roleRequest));

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

    @Test
    public void testGetRoleRequests() {
        int page = 1;
        int size = 40;
        List<RoleRequest> roleRequests = Arrays.asList(roleRequest);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(roleRequestRepository.findAll(pageable)).thenReturn(roleRequestPage);
        when(roleRequestPage.getContent()).thenReturn(roleRequests);

        List<RoleRequest> result = roleRequestService.getRoleRequests(page, size);

        verify(roleRequestRepository).findAll(pageable);
        verifyNoMoreInteractions(roleRequestRepository);

        assertEquals("A list of rolerequests is returned", roleRequests, result);
    }
}
