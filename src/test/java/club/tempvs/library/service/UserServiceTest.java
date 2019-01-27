package club.tempvs.library.service;

import static org.mockito.Mockito.*;

import club.tempvs.library.dao.UserRepository;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setup() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testSaveUser() {
        Long userId = 1L;
        String userName = "name";
        Long userProfileId = 2L;
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setUserName(userName);
        userInfoDto.setUserProfileId(userProfileId);
        User user = new User(userInfoDto);

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(userInfoDto);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);

        Assert.assertEquals("User is returned as a result", user, result);
    }
}
