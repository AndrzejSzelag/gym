package pl.szelag.gym.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.identity.UserRole;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AuthUser mapping and security behavior.
 */
class AuthUserTest {

    @Test
    void shouldMapUserEntityToAuthUserCorrectly() {
        // GIVEN
        Role adminRole = RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority());
        User user = new User(
                "Andrzej",
                "Szelag",
                "andrzej.szelag@gym.pl",
                "hashed_pass",
                adminRole
        );

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");

        // WHEN
        AuthUser authUser = new AuthUser(user, List.of(authority));

        // THEN
        assertThat(authUser.getUsername()).isEqualTo("andrzej.szelag@gym.pl");
        assertThat(authUser.getFullName()).isEqualTo("Andrzej Szelag");
        assertThat(authUser.getPassword()).isEqualTo("hashed_pass");

        // Fix for the compilation error: extract 'authority' string from the objects
        assertThat(authUser.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMINISTRATOR");
    }

    @Test
    void shouldErasePasswordWhenRequested() {
        // GIVEN
        Role role = RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority());
        User user = new User("Andrzej", "Szelag", "andrzej.szelag@gym.pl", "secret", role);
        AuthUser authUser = new AuthUser(user, List.of());

        // WHEN
        authUser.eraseCredentials();

        // THEN
        assertThat(authUser.getPassword())
                .as("Password must be null after credentials erasure")
                .isNull();
    }
}