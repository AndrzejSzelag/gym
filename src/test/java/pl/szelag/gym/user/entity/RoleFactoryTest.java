package pl.szelag.gym.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.szelag.gym.user.identity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleFactoryTest {

    @Test
    void givenUserRoleEnum_whenCreatingFromEnum_thenReturnsCorrectRole() {
        // GIVEN
        UserRole adminRoleEnum = UserRole.ADMINISTRATOR;

        // WHEN
        Role role = RoleFactory.fromEnum(adminRoleEnum);

        // THEN
        assertThat(role.getName()).isEqualTo("ROLE_ADMINISTRATOR");
    }

    @Test
    void givenNullEnum_whenCreatingFromEnum_thenThrowsNullPointerException() {
        // GIVEN
        UserRole nullEnum = null;

        // WHEN & THEN
        assertThatThrownBy(() -> RoleFactory.fromEnum(nullEnum))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("required");
    }

    @Test
    void givenRawInputName_whenCreatingSystemRole_thenNameIsNormalizedToUppercaseAndTrimmed() {
        // GIVEN
        String rawInput = "   role_administrator   ";

        // WHEN
        Role role = RoleFactory.systemRole(rawInput);

        // THEN
        assertThat(role.getName()).isEqualTo("ROLE_ADMINISTRATOR");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void givenBlankName_whenCreatingSystemRole_thenThrowsIllegalArgumentException(String invalidName) {
        // WHEN & THEN
        assertThatThrownBy(() -> RoleFactory.systemRole(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    void givenNullName_whenCreatingSystemRole_thenThrowsIllegalArgumentException() {
        // GIVEN
        String nullName = null;

        // WHEN & THEN
        assertThatThrownBy(() -> RoleFactory.systemRole(nullName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}