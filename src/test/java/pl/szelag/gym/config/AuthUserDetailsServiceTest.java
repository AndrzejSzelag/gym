package pl.szelag.gym.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthUserDetailsService.
 * Verifies that the service correctly interacts with the repository and maps
 * domain User entities to Spring Security UserDetails.
 */
@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthUserDetailsService authUserDetailsService;

    @Test
    void shouldLoadUserByUsernameAndCorrectlyApplyRolePrefix() {
        // GIVEN
        String inputEmail = " ANDRZEJ.szelag@gym.pl ";
        String normalizedEmail = "andrzej.szelag@gym.pl";

        // Scenario: Role in DB does not have the 'ROLE_' prefix
        Role rawRole = RoleFactory.systemRole("ADMINISTRATOR");
        User user = new User(
                "Andrzej",
                "Szelag",
                normalizedEmail,
                "encoded_password",
                rawRole
        );

        when(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(user));

        // WHEN
        UserDetails result = authUserDetailsService.loadUserByUsername(inputEmail);

        // THEN
        assertThat(result).isInstanceOf(AuthUser.class);
        assertThat(result.getUsername()).isEqualTo(normalizedEmail);

        // Verifying that the service correctly appended "ROLE_" prefix
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMINISTRATOR");

        AuthUser authUser = (AuthUser) result;
        assertThat(authUser.getFullName()).isEqualTo("Andrzej Szelag");
        assertThat(authUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenEmailDoesNotExist() {
        // GIVEN
        String email = "nonexistent@gym.pl";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> authUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Security error: invalid credentials");
    }
}