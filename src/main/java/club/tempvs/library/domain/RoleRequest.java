package club.tempvs.library.domain;

import club.tempvs.library.model.Role;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class RoleRequest {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private Role role;

    @CreatedDate
    private Instant createdDate;
}
