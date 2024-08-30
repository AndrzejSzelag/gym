package pl.szelag.gym;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.szelag.gym.user.*;
import pl.szelag.gym.user.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User userBuilder1L;

    @BeforeEach
    void init() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository, roleRepository, passwordEncoder);

        userBuilder1L = User.builder()
                .id(1L)
                .name("Andrzej Szelag")
                .email("andrzejszelag@ea.pl")
                .password(passwordEncoder.encode("123456789"))
                .build();
    }

    @Test
    @DisplayName("Test for getUsers() method [POSITIVE SCENARIO]")
    void givenUserList_whenGetUsers_thenReturnUserList() {

        // given
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
        User userBuilder2L = User.builder()
                .id(2L)
                .name("Nikodem Szelag")
                .email("nikodemszelag@ea.pl")
                .password(passwordEncoder.encode("987654321"))
                .build();
        given(userRepository.findAll()).willReturn(List.of(userBuilder1L, userBuilder2L));

        // when
        List<UserDto> userList = userService.getUsers();

        // then
        Assertions.assertNotNull(userList);
        assertThat(userList).hasSize(2);
        verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test for getUsers() method [NEGATIVE SCENARIO]")
    void givenEmptyUserList_whenGetUsers_thenReturnEmptyUserList() {

        // given
        given(userRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<UserDto> userList = userService.getUsers();

        // then
        assertThat(userList).isEmpty();
        verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test for getUser()_byId method")
    void givenUserById_whenGetUser_thenReturnUser() {

        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(userBuilder1L));

        // when
        User savedUser = userService.getUser(userBuilder1L.getId());

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);
        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test for getUser()_byEmail method")
    void givenUserByEmail_whenGetUser_thenReturnUser() {

        // given
        given(userRepository.findByEmail("andrzejszelag@ea.pl")).willReturn(userBuilder1L);

        // when
        User savedUser = userService.getUser(userBuilder1L.getEmail());

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("andrzejszelag@ea.pl");
        verify(userRepository, Mockito.times(1)).findByEmail("andrzejszelag@ea.pl");
    }

    @Test
    @DisplayName("Test for registerUser() method with EmptyUser")
    void givenUserObject_whenRegisterUser_thenReturnEmptyUser() {

        // given
        User userBuilderEmptyUser = User.builder()
                .roles(Collections.singleton(Role.builder()
                        .build()))
                .build();

        // when
        userRepository.save(userBuilderEmptyUser);
        userRepository.save(userBuilderEmptyUser);

        // then
        assertThat(userBuilderEmptyUser.getId()).isNull();
    }

    @Test
    @DisplayName("Test for registerUser() method")
    void givenUserObject_whenRegisterUser_thenReturnUser() {

        // given
        UserDto userBuilderDto = UserDto.builder().build();

        userBuilderDto.setFirstName("Emilia");
        userBuilderDto.setLastName("Szelag");
        userBuilderDto.setEmail("emiliaszelag@ea.pl");
        userBuilderDto.setPassword("777777777");

        User userBuilder3L = User.builder()
                .roles(Collections.singleton(Role.builder()
                        .id(1L)
                        .name("ADMINISTRATOR")
                        .build()))
                .build();

        userBuilder3L.setName(userBuilderDto.getFirstName() + " " + userBuilderDto.getLastName());
        userBuilder3L.setEmail(userBuilderDto.getEmail());
        userBuilder3L.setPassword(userBuilderDto.getPassword());

        // when
        userService.registerUser(userBuilderDto);
        userRepository.save(userBuilder3L);

        // then
        Assertions.assertNotNull(userBuilderDto);
        Assertions.assertNotNull(userBuilder3L);
        assertThat(userBuilder3L.getEmail()).isEqualTo("emiliaszelag@ea.pl");
        verify(userRepository, Mockito.times(1)).save(userBuilder3L);
    }
}