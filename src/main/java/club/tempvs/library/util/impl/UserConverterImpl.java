package club.tempvs.library.util.impl;

import club.tempvs.library.model.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.util.UserConverter;
import org.springframework.stereotype.Component;

@Component
public class UserConverterImpl implements UserConverter {

    public User convert(UserInfoDto userInfoDto) {
        return new User(userInfoDto);
    }
}
