package club.tempvs.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserRolesDto {

    private Long id;
    private List<String> roles;

    public UserRolesDto(Long id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }
}
