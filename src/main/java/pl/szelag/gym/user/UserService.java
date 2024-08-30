package pl.szelag.gym.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.exception.NotFoundException;
import pl.szelag.gym.user.dto.UserDto;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(User::getId).reversed())
                .map(user -> mapUserToUserDto(user))
                .toList();
    }

    private UserDto mapUserToUserDto(User user) {
        UserDto userDto = UserDto.builder().build();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User getUser(final Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not exist!"));
    }

    public User getUser(final String email) {
        return userRepository.findByEmail(email);
    }

    public void registerUser(UserDto userDto) {
        User user = User.builder().build();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ADMINISTRATOR");
        if (role == null) {
            role = checkIsRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    private Role checkIsRoleExist() {
        Role role = Role.builder().build();
        role.setName("ADMINISTRATOR");
        return roleRepository.save(role);
    }
}