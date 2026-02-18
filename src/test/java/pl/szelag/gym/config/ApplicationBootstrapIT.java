package pl.szelag.gym.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;
import pl.szelag.gym.user.service.RoleInitializationService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the bootstrap process.
 * Since ApplicationBootstrap is disabled in 'test' profile,
 * we manually trigger the initialization service to verify its logic.
 */
class ApplicationBootstrapIT extends BaseIT {

    @Autowired
    private RoleInitializationService roleInitializationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
    }

    @Test
    void shouldInitializeAdministratorRoleWhenServiceIsCalled() {
        // GIVEN
        String adminRole = "ROLE_ADMINISTRATOR";
        roleRepository.deleteAllInBatch(); // Ensure clean state

        // WHEN
        // We manually trigger the service because @Profile("!test") excludes
        // the ApplicationBootstrap bean from the current test context.
        roleInitializationService.initializeRoles();

        // THEN
        assertThat(roleRepository.findByName(adminRole))
                .as("The role %s should be created by RoleInitializationService", adminRole)
                .isPresent();
    }
}