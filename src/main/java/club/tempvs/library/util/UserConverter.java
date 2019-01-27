package club.tempvs.library.util;

import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;

public interface UserConverter {

    User convert(UserInfoDto userInfoDto);
}
