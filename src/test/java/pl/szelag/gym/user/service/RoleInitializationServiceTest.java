package pl.szelag.gym.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleInitializationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializationService roleInitializationService;

    @Test
    void shouldCreateMissingRolesWhenDatabaseIsEmpty() {
        // // GIVEN
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // WHEN
        roleInitializationService.initializeRoles();

        // THEN
        // Verifies that save is called for each defined UserRole (ADMINISTRATOR, GUEST, etc.)
        verify(roleRepository, times(UserRole.values().length)).save(any(Role.class));
    }

    @Test
    void shouldNotCreateRolesWhenTheyAlreadyExist() {
        // // GIVEN
        Role existingRole = RoleFactory.fromEnum(UserRole.ADMINISTRATOR);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(existingRole));

        // WHEN
        roleInitializationService.initializeRoles();

        // THEN
        // Should find existing roles and never call save
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldSpecificallyCreateAdministratorRole() {
        // // GIVEN
        String adminAuthority = UserRole.ADMINISTRATOR.authority();
        when(roleRepository.findByName(adminAuthority)).thenReturn(Optional.empty());
        // Mocking other roles as existing to isolate Administrator
        when(roleRepository.findByName(UserRole.GUEST.authority()))
                .thenReturn(Optional.of(RoleFactory.fromEnum(UserRole.GUEST)));

        // WHEN
        roleInitializationService.initializeRoles();

        // THEN
        verify(roleRepository).findByName(adminAuthority);
        verify(roleRepository).save(argThat(role -> role.getName().equals(adminAuthority)));
    }
}