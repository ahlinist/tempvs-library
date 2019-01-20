package club.tempvs.library.model;

import static java.util.stream.Collectors.toList;

import club.tempvs.library.dto.UserInfoDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
public class User {

    private Long id;
    private Long profileId;
    private String timezone;
    private Locale locale;
    private List<Role> roles;

    public User(UserInfoDto userInfoDto) {
        List<String> stringRoles = Role.stringValues;

        this.id = userInfoDto.getUserId();
        this.profileId = userInfoDto.getProfileId();
        this.timezone = userInfoDto.getTimezone();
        this.locale = new Locale(userInfoDto.getLang());
        this.roles = userInfoDto.getRoles().stream()
                .filter(stringRoles::contains)
                .map(Role::valueOf)
                .collect(toList());
    }
}
