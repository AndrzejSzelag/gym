package pl.szelag.gym.user.identity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void givenAdministratorRole_whenRequestingAuthority_thenReturnsFormattedSpringSecurityString() {
        // GIVEN
        UserRole role = UserRole.ADMINISTRATOR;

        // WHEN
        String authority = role.authority();

        // THEN
        assertThat(authority).isEqualTo("ROLE_ADMINISTRATOR");
    }

    @Test
    void givenAdministratorRole_whenConvertingToGrantedAuthority_thenReturnsSimpleGrantedAuthorityWithCorrectRole() {
        // GIVEN
        UserRole role = UserRole.ADMINISTRATOR;

        // WHEN
        GrantedAuthority grantedAuthority = role.toGrantedAuthority();

        // THEN
        assertThat(grantedAuthority.getAuthority()).isEqualTo("ROLE_ADMINISTRATOR");
    }

    @Test
    void givenAdministratorRole_whenRequestingRoleName_thenReturnsRawDomainName() {
        // GIVEN
        UserRole role = UserRole.ADMINISTRATOR;

        // WHEN
        String roleName = role.getRoleName();

        // THEN
        assertThat(roleName).isEqualTo("ADMINISTRATOR");
    }

    @Test
    void givenAdministratorRole_whenCallingToString_thenReturnsDomainRoleName() {
        // GIVEN
        UserRole role = UserRole.ADMINISTRATOR;

        // WHEN
        String value = role.toString();

        // THEN
        assertThat(value).isEqualTo("ADMINISTRATOR");
    }

    @Test
    void givenGuestRole_whenCheckingProperties_thenMatchesGuestConstants() {
        // GIVEN
        UserRole role = UserRole.GUEST;

        // WHEN & THEN
        assertThat(role.authority()).isEqualTo("ROLE_GUEST");
        assertThat(role.getRoleName()).isEqualTo("GUEST");
    }
}