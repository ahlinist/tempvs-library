package club.tempvs.library.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminPanelPageDto {

    private List<RoleRequestDto> roleRequests;

    public AdminPanelPageDto(List<RoleRequestDto> roleRequests) {
        this.roleRequests = roleRequests;
    }
}
