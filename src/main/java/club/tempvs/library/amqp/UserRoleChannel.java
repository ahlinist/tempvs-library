package club.tempvs.library.amqp;

import club.tempvs.library.dto.UserRolesDto;

public interface UserRoleChannel {

    void updateRoles(UserRolesDto userRolesDto);
}
