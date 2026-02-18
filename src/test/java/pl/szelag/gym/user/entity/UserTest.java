package pl.szelag.gym.user.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static final String TEST_EMAIL = "andrzej.szelag@gym.pl";
    private static final String TEST_FIRST_NAME = "Andrzej";
    private static final String TEST_LAST_NAME = "Szelag";
    private static final String TEST_PASSWORD = "securePassword123";
    private static final String ADMIN_ROLE_NAME = "ROLE_ADMINISTRATOR";

    private Role adminRole;

    @BeforeEach
    void setUp() {
        // GIVEN
        adminRole = new Role(ADMIN_ROLE_NAME);
    }

    @Test
    void shouldCreateUserWithCorrectData() {
        // GIVEN
        // Constants used for test data

        // WHEN
        User user = new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, adminRole);

        // THEN
        assertThat(user.getFirstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(TEST_LAST_NAME);
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(user.getRole().getName()).isEqualTo(ADMIN_ROLE_NAME);
    }

    @Test
    void shouldUpdateUserProfileCorrectly() {
        // GIVEN
        User user = new User("Old", "Name", "old@gym.pl", TEST_PASSWORD, adminRole);

        // WHEN
        user.updateUserProfile(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        // THEN
        assertThat(user.getFirstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(TEST_LAST_NAME);
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void shouldReturnFormattedFullName() {
        // GIVEN
        User user = new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, adminRole);

        // WHEN
        String fullName = user.getFullName();

        // THEN
        assertThat(fullName).isEqualTo("Andrzej Szelag");
    }

    @Test
    void shouldHandleNullNamesInFullNameGracefully() {
        // GIVEN
        User user = new User(null, null, TEST_EMAIL, TEST_PASSWORD, adminRole);

        // WHEN
        String fullName = user.getFullName();

        // THEN
        assertThat(fullName).isEmpty();
    }
}