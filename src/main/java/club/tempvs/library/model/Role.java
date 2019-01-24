package club.tempvs.library.model;

import static java.util.stream.Collectors.toList;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum Role {

    ROLE_USER("role.user"),
    ROLE_ADMIN("role.admin"),
    ROLE_ARCHIVARIUS("role.archivarius"),
    ROLE_SCRIBE("role.scribe"),
    ROLE_CONTRIBUTOR("role.contributor");

    public static List<String> stringValues = Arrays.stream(Role.values()).map(Role::toString).collect(toList());

    @Getter
    String key;

    Role(String key) {
        this.key = key;
    }
}
