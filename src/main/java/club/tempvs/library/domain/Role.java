package club.tempvs.library.domain;

import lombok.Getter;

public enum Role {

    ROLE_ADMIN("role.admin"),
    ROLE_ARCHIVARIUS("role.archivarius"),
    ROLE_SCRIBE("role.scribe"),
    ROLE_CONTRIBUTOR("role.contributor");

    @Getter
    String key;

    Role(String key) {
        this.key = key;
    }
}
