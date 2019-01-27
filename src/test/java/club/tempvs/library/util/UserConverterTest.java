package club.tempvs.library.util;

import static org.junit.Assert.*;
import static java.util.stream.Collectors.toList;

import club.tempvs.library.model.Role;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.util.impl.UserConverterImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserConverterTest {

    private UserConverter userConverter;

    @Before
    public void setup() {
        userConverter = new UserConverterImpl();
    }

    @Test
    public void testConvert() {
        Long userId = 1L;
        Long profileId = 2L;
        String timezone = "UTC";
        String lang = "en";
        List<String> roles = Arrays.asList("ROLE_SCRIBE");
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setProfileId(profileId);
        userInfoDto.setTimezone(timezone);
        userInfoDto.setLang(lang);
        userInfoDto.setRoles(roles);

        User result = userConverter.convert(userInfoDto);

        assertEquals("User has the same id as userInfoDto", userInfoDto.getUserId(), result.getId());
        assertEquals("User has the same ProfileId as userInfoDto", userInfoDto.getProfileId(), result.getProfileId());
        assertEquals("User has the same timezone as userInfoDto", userInfoDto.getTimezone(), result.getTimezone());
        assertEquals("User has the same lang as userInfoDto", userInfoDto.getLang(), result.getLocale().getLanguage());
        assertEquals("User has the same roles as userInfoDto", userInfoDto.getRoles(),
                result.getRoles().stream().map(Role::toString).collect(toList()));
    }
}
