package club.tempvs.library.dto;

import club.tempvs.library.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequestDto {

    private Long userId;
    private Long userProfileId;
    private String userName;
    private String role;

    public RoleRequestDto(User user, String role) {
        this.userId = user.getId();
        this.userProfileId = user.getUserProfileId();
        this.userName = user.getUserName();
        this.role = role;
    }
}
