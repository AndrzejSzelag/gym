package pl.szelag.gym.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserReadServiceImplIT extends BaseIT {

    @Autowired
    private UserReadService userReadService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void givenSavedUser_whenFindByEmail_thenReturnsUserFromDatabase() {
        // // GIVEN
        String email = "andrzej.szelag@gym.pl";
        Role role = roleRepository.findByName(UserRole.ADMINISTRATOR.authority())
                .orElseGet(() -> roleRepository.save(RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority())));

        userRepository.save(new User("Andrzej", "Szelag", email, "pass", role));

        // WHEN
        Optional<User> foundUser = userReadService.findByEmail(email);

        // THEN
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
        assertThat(foundUser.get().getFirstName()).isEqualTo("Andrzej");
    }

    @Test
    void givenMultipleUsersInDatabase_whenGetUsers_thenReturnsCorrectListOfDtos() {
        // // GIVEN
        userRepository.deleteAllInBatch();
        Role role = roleRepository.findByName(UserRole.ADMINISTRATOR.authority())
                .orElseGet(() -> roleRepository.save(RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority())));

        userRepository.save(new User("Andrzej", "Szelag", "andrzej.szelag@gym.pl", "pass", role));
        userRepository.save(new User("Jan", "Kowalski", "jan.k@gym.pl", "pass", role));

        // WHEN
        List<UserDto> users = userReadService.getUsers();

        // THEN
        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::email)
                .containsExactlyInAnyOrder("andrzej.szelag@gym.pl", "jan.k@gym.pl");
    }
}