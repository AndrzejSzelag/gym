package pl.szelag.gym.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.identity.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class RoleRepositoryIT extends BaseIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        // // GIVEN: Clean database to remove side effects from DataInitializer
        // First delete users due to foreign key constraints, then roles
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
    }

    @Test
    void givenStandardizedRole_whenSavingAndFindingByName_thenRoleIsCorrectlyRetrieved() {
        // // GIVEN
        String authority = UserRole.ADMINISTRATOR.authority();
        Role roleToSave = RoleFactory.systemRole(authority);

        // WHEN
        roleRepository.saveAndFlush(roleToSave);
        Optional<Role> found = roleRepository.findByName(authority);

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ROLE_ADMINISTRATOR");
    }

    @Test
    void givenCleanDatabase_whenFindingNonExistentRole_thenReturnsEmptyOptional() {
        // // GIVEN
        String unknownRole = "NON_EXISTENT_ROLE";

        // WHEN
        Optional<Role> found = roleRepository.findByName(unknownRole);

        // THEN
        assertThat(found).isEmpty();
    }
}