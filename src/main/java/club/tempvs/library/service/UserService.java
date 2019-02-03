package club.tempvs.library.service;

import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserDto;

public interface UserService {

    User saveUser(UserDto userDto);

    User getUser(Long id);
}
