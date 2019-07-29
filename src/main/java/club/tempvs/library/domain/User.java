package club.tempvs.library.domain;

import static java.util.stream.Collectors.toList;

import club.tempvs.library.dto.UserDto;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@Entity(name = "app_user")
public class User {

    @Id
    private Long id;
    @NotNull
    private Long userProfileId;
    @NotBlank
    private String userName;
    private transient Locale locale;
    private transient List<Role> roles;

    public User(UserInfoDto userInfoDto) {
        this.id = userInfoDto.getUserId();
        this.userName = userInfoDto.getUserName();
        this.locale = new Locale(userInfoDto.getLang());
        this.roles = userInfoDto.getRoles().stream()
                .filter(Role.getStringValues()::contains)
                .map(Role::valueOf)
                .collect(toList());
    }

    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.userProfileId = userDto.getUserProfileId();
        this.userName = userDto.getUserName();
    }
}
