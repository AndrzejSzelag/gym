package pl.szelag.gym.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.common.exception.DuplicateResourceException;
import pl.szelag.gym.common.exception.InvalidDataException;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.factory.UserFactory;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;

/** Service handling user lifecycle commands such as registration and role assignment. */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param userDto registration data
     * @throws DuplicateResourceException if email exists
     * @throws InvalidDataException if role is missing */
    public void registerNewUser(UserDto userDto) {
        log.debug("Registering new user: {}", userDto.email());

        validateUserDto(userDto);
        checkEmailUniqueness(userDto.email());

        String encodedPassword = passwordEncoder.encode(userDto.password());

        Role defaultRole = roleRepository.findByName(UserRole.ADMINISTRATOR.authority())
                .orElseThrow(() -> {
                    log.error("Critical error: Role {} not found", UserRole.ADMINISTRATOR);
                    return new InvalidDataException("error.role.not.found", UserRole.ADMINISTRATOR);
                });

        User user = UserFactory.fromRegistration(
                UserDto.builder()
                        .firstName(userDto.firstName())
                        .lastName(userDto.lastName())
                        .email(userDto.email())
                        .password(encodedPassword)
                        .build(),
                defaultRole
        );

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
    }

    /** @param email address to verify @throws DuplicateResourceException if email is already taken */
    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email {} already exists", email);
            throw new DuplicateResourceException("USER", "error.user.email.already.exists", email);
        }
    }

    /** @param userDto data to validate @throws InvalidDataException if password is null or blank */
    private static void validateUserDto(UserDto userDto) {
        if (userDto.password() == null || userDto.password().trim().isEmpty()) {
            throw new InvalidDataException("error.password.required");
        }
    }
}