package club.tempvs.library.service;

import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;

public interface UserService {
    User saveUser(UserInfoDto userInfoDto);
}
