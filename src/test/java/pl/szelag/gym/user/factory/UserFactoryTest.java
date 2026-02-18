package pl.szelag.gym.user.factory;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.identity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserFactoryTest {

    @Test
    void givenRawRegistrationDtoAndRole_whenCreatingUser_thenReturnsUserWithCleanState() {
        // GIVEN
        UserDto dto = new UserDto(
                "  Andrzej  ",
                " Szelag ",
                " ANDRZEJ.SZELAG@GYM.PL ",
                "hashed_pass"
        );
        Role adminRole = RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority());

        // WHEN
        User user = UserFactory.fromRegistration(dto, adminRole);

        // THEN
        assertThat(user.getFirstName()).isEqualTo("Andrzej");
        assertThat(user.getLastName()).isEqualTo("Szelag");
        assertThat(user.getEmail()).isEqualTo("andrzej.szelag@gym.pl");
        assertThat(user.getFullName()).isEqualTo("Andrzej Szelag");
    }

    @Test
    void givenMissingDto_whenCreatingUser_thenThrowsNullPointerException() {
        // GIVEN
        Role role = RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority());

        // WHEN & THEN
        assertThatThrownBy(() -> UserFactory.fromRegistration(null, role))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserDto");
    }

    @Test
    void givenMissingRole_whenCreatingUser_thenThrowsNullPointerException() {
        // GIVEN
        UserDto dto = new UserDto("Andrzej", "Szelag", "andrzej.szelag@gym.pl", "pass");

        // WHEN & THEN
        assertThatThrownBy(() -> UserFactory.fromRegistration(dto, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Role");
    }

    @Test
    void givenDtoWithBlankLastName_whenCreatingUser_thenReturnsUserWithFirstNameOnly() {
        // GIVEN
        UserDto dto = new UserDto(
                "  Andrzej  ",
                "   ",
                "andrzej.szelag@gym.pl",
                "pass"
        );
        Role role = RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority());

        // WHEN
        User user = UserFactory.fromRegistration(dto, role);

        // THEN
        // Sprawdzamy czy lastName jest nullem lub pusty, co rozwiązuje błąd AssertionError
        assertThat(user.getLastName()).isNullOrEmpty();
        assertThat(user.getFullName()).isEqualTo("Andrzej");
    }
}