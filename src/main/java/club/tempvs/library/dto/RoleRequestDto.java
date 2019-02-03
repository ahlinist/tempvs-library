package club.tempvs.library.dto;

import club.tempvs.library.domain.User;
import club.tempvs.library.model.Role;
import lombok.Data;

@Data
public class RoleRequestDto {

    private Long userId;
    private Long userProfileId;
    private String userName;
    private String role;
    private String roleLabel;

    public RoleRequestDto(User user, Role role, String roleLabel) {
        this.userId = user.getId();
        this.userProfileId = user.getUserProfileId();
        this.userName = user.getUserName();
        this.role = role.toString();
        this.roleLabel = roleLabel;
    }
}
