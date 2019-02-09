package club.tempvs.library.service;

import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserDto;
import club.tempvs.library.dto.UserRolesDto;

public interface UserService {

    User saveUser(UserDto userDto);

    User getUser(Long id);

    void updateUserRoles(UserRolesDto userRolesDto);
}
