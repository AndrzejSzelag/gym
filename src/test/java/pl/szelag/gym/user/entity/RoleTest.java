package pl.szelag.gym.user.entity;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.user.identity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void givenAuthorityName_whenCreatingRoleEntity_thenNameIsStoredAndAuditingFieldsAreNull() {
        // GIVEN
        String roleName = UserRole.ADMINISTRATOR.authority();

        // WHEN
        Role role = new Role(roleName);

        // THEN
        assertThat(role.getName()).isEqualTo("ROLE_ADMINISTRATOR");
        assertThat(role.getId()).isNull();

        assertThat(role.getCreatedAt()).isNull();
        assertThat(role.getCreatedBy()).isNull();
        assertThat(role.getUpdatedAt()).isNull();
        assertThat(role.getLastModifiedBy()).isNull();
    }
}