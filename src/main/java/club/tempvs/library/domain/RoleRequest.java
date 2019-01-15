package club.tempvs.library.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class RoleRequest {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private Role role;
}
