package pl.szelag.gym.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;

import java.util.Arrays;

/** Service for bootstrapping mandatory security roles in the database. */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleInitializationService {

    private final RoleRepository roleRepository;

    /** Synchronizes domain UserRole enum with the database by creating missing roles. */
    @Transactional
    public void initializeRoles() {
        log.debug("Synchronizing security roles...");
        Arrays.stream(UserRole.values()).forEach(this::ensureRoleExists);
        log.info("Security roles synchronization completed.");
    }

    /** @param role domain role to be verified and persisted if missing */
    private void ensureRoleExists(UserRole role) {
        String authority = role.authority();
        roleRepository.findByName(authority)
                .ifPresentOrElse(r -> log.debug("Role '{}' already exists.", authority),
                        () -> {
                            roleRepository.save(RoleFactory.systemRole(authority));
                            log.info("Created missing role: '{}'", authority);
                        }
                );
    }
}