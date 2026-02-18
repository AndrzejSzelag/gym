package pl.szelag.gym.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.szelag.gym.common.exception.DuplicateResourceException;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Fixes UnnecessaryStubbingException
class UserCommandServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCommandService userCommandService;

    @Test
    void givenExistingEmail_whenRegistering_thenThrowsDuplicateResourceException() {
        // GIVEN
        String email = "andrzej.szelag@gym.pl";
        UserDto dto = new UserDto("Andrzej", "Szelag", email, "pass");

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> userCommandService.registerNewUser(dto))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository).existsByEmail(email);
        verifyNoInteractions(passwordEncoder); // Best practice: ensure no heavy ops if fail fast
    }
}