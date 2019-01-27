package club.tempvs.library.domain;

import static java.util.stream.Collectors.toList;

import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    private Long id;
    private transient Long profileId;
    private Long userProfileId;
    private String userName;
    private transient String timezone;
    private transient Locale locale;
    private transient List<Role> roles;

    public User(UserInfoDto userInfoDto) {
        List<String> stringRoles = Role.getStringValues();

        this.id = userInfoDto.getUserId();
        this.profileId = userInfoDto.getProfileId();
        this.userName = userInfoDto.getUserName();
        this.timezone = userInfoDto.getTimezone();
        this.locale = new Locale(userInfoDto.getLang());
        this.roles = userInfoDto.getRoles().stream()
                .filter(stringRoles::contains)
                .map(Role::valueOf)
                .collect(toList());
    }
}
