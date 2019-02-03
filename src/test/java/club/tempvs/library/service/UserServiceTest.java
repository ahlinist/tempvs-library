package club.tempvs.library.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.library.dao.UserRepository;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserDto;
import club.tempvs.library.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private User user;
    @Mock
    private UserRepository userRepository;

    @Before
    public void setup() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testSaveUserForUserInfoDto() {
        Long userId = 1L;
        String userName = "name";
        Long userProfileId = 2L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUserName(userName);
        userDto.setUserProfileId(userProfileId);
        User user = new User(userDto);

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(userDto);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);

        assertEquals("User is returned as a result", user, result);
    }

    @Test
    public void testGetUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUser(userId);

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);

        assertEquals("User is returned as a result", user, result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetUserIfNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userService.getUser(userId);
    }
}
