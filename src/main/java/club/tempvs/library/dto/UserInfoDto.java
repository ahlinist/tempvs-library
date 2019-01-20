package club.tempvs.library.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoDto {
    private Long userId;
    private Long profileId;
    private String timezone;
    private String lang;
    private List<String> roles = new ArrayList<>();
}
