package club.tempvs.library.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoDto {
    private Long userId;
    private Long userProfileId;
    private String userName;
    private String lang = "en";
    private List<String> roles = new ArrayList<>();
}
