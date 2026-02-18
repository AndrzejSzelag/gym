package pl.szelag.gym.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.factory.UserFactory;
import pl.szelag.gym.user.identity.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class UserRepositoryIT extends BaseIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;

    @BeforeEach
    void setup() {
        // // GIVEN: Ensure the mandatory ADMINISTRATOR role exists in the DB
        userRepository.deleteAllInBatch();
        adminRole = roleRepository.findByName(UserRole.ADMINISTRATOR.authority())
                .orElseGet(() -> roleRepository.save(RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority())));
    }

    @Test
    void givenSavedUser_whenFindingByEmail_thenReturnsUserWithAllFieldsMapped() {
        // // GIVEN
        String email = "andrzej.szelag@gym.pl";
        UserDto dto = new UserDto("Andrzej", "Szelag", email, "secret_hash");
        userRepository.save(UserFactory.fromRegistration(dto, adminRole));

        // WHEN
        Optional<User> found = userRepository.findByEmail(email);

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Andrzej");
        assertThat(found.get().getLastName()).isEqualTo("Szelag");
        assertThat(found.get().getRole().getName()).isEqualTo(UserRole.ADMINISTRATOR.authority());
    }

    @Test
    void givenExistingUser_whenSavingAnotherWithSameEmail_thenThrowsDataIntegrityViolationException() {
        // // GIVEN
        String sharedEmail = "unique@gym.pl";
        User firstUser = UserFactory.fromRegistration(
                new UserDto("Jan", "Kowalski", sharedEmail, "p1"), adminRole);
        userRepository.saveAndFlush(firstUser);

        // WHEN
        User duplicateUser = UserFactory.fromRegistration(
                new UserDto("Adam", "Nowak", sharedEmail, "p2"), adminRole);

        // THEN
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateUser);
        });
    }

    @Test
    void givenUserWithRole_whenSaved_thenMaintainsDatabaseRelationship() {
        // // GIVEN
        UserDto dto = new UserDto("Anna", "Nowak", "anna@gym.pl", "pass");
        User user = UserFactory.fromRegistration(dto, adminRole);

        // WHEN
        User savedUser = userRepository.save(user);

        // THEN
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getRole()).isEqualTo(adminRole);
    }

    @Test
    void givenExistingUser_whenCheckingExistenceByEmail_thenReturnsTrue() {
        // // GIVEN
        String email = "exists@gym.pl";
        userRepository.save(UserFactory.fromRegistration(
                new UserDto("Jan", "Kowalski", email, "p1"), adminRole));

        // WHEN
        boolean exists = userRepository.existsByEmail(email);

        // THEN
        assertThat(exists).isTrue();
    }
}