package club.tempvs.library.domain;

import club.tempvs.library.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RoleRequest {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    private Role role;

    @CreatedDate
    private Instant createdDate;

    public RoleRequest(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
