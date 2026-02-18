package pl.szelag.gym.user.factory;

import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.User;

import java.util.Objects;

/** Domain factory for consistent {@link User} instantiation. */
public final class UserFactory {

    private UserFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** @param dto registration data @param role assigned authority new User entity */
    public static User fromRegistration(UserDto dto, Role role) {
        Objects.requireNonNull(dto, "UserDto cannot be null");
        Objects.requireNonNull(role, "Role cannot be null");

        return new User(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.password(),
                role
        );
    }
}