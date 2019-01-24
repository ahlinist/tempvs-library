package club.tempvs.library.domain;

import club.tempvs.library.model.Role;
import club.tempvs.library.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RoleRequest {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private Role role;

    @CreatedDate
    private Instant createdDate;

    public RoleRequest(User user, Role role) {
        this.userId = user.getId();
        this.role = role;
    }
}
