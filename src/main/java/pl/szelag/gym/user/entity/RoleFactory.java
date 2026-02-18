package pl.szelag.gym.user.entity;

import pl.szelag.gym.user.identity.UserRole;

import java.util.Objects;

/** Domain factory for {@link Role} instantiation. */
public final class RoleFactory {

    private RoleFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** @param roleEnum domain enum source new Role entity */
    public static Role fromEnum(UserRole roleEnum) {
        Objects.requireNonNull(roleEnum, "Role enum is required");
        return new Role(roleEnum.authority());
    }

    /** @param name raw role name new Role entity with trimmed uppercase name */
    public static Role systemRole(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        return new Role(name.trim().toUpperCase());
    }
}