package pl.szelag.gym.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.common.exception.DuplicateResourceException;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.factory.UserFactory;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserCommandServiceIT extends BaseIT {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // GIVEN
        // Czyścimy wszystko w odpowiedniej kolejności (FK constraints!)
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();

        // Zawsze upewniamy się, że rola istnieje przed każdym testem
        roleRepository.saveAndFlush(RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority()));
    }

    @Test
    void givenRegistrationDto_whenRegisteringNewUser_thenUserIsSavedWithEncodedPasswordAndRole() {
        // GIVEN
        String email = "andrzej.szelag@gym.pl";
        String rawPassword = "rawPassword123";
        UserDto registrationDto = new UserDto("Andrzej", "Szelag", email, rawPassword);

        // WHEN
        userCommandService.registerNewUser(registrationDto);

        // THEN
        Optional<User> savedUser = userRepository.findByEmail(email);
        assertThat(savedUser).isPresent();
        assertThat(passwordEncoder.matches(rawPassword, savedUser.get().getPassword())).isTrue();
    }

    @Test
    void givenExistingUser_whenRegisteringWithSameEmail_thenThrowsDuplicateResourceException() {
        // GIVEN
        String email = "andrzej.szelag@gym.pl";
        Role adminRole = roleRepository.findByName(UserRole.ADMINISTRATOR.authority()).orElseThrow();

        userRepository.saveAndFlush(UserFactory.fromRegistration(
                new UserDto("Existing", "User", email, "pass"), adminRole));

        UserDto newDto = new UserDto("Andrzej", "Szelag", email, "newPass");

        // WHEN & THEN
        assertThatThrownBy(() -> userCommandService.registerNewUser(newDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("error.user.email.already.exists");
    }
}