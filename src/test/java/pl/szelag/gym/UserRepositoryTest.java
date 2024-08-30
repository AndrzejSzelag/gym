package pl.szelag.gym;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.szelag.gym.user.User;
import pl.szelag.gym.user.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Test
    @DisplayName("UserRepositoryLayerTest")
    void givenUser_whenSavedUser_thenCanBeFoundByEmail() {

        // given
        user = new User();
        user.setName("Andrzej Szelag");
        user.setEmail("andrzejszelag@ea.pl");
        user.setPassword("123456789");

        // when
        User savedUser = userRepository.saveAndFlush(user);
        User existUser = entityManager.find(User.class, savedUser.getId());

        // then
        assertNotNull(savedUser);
        assertEquals(user.getName(), savedUser.getName());
        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
        assertEquals(user.getPassword(), savedUser.getPassword());
    }
}
