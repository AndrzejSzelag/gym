package pl.szelag.gym;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import pl.szelag.gym.user.UserController;
import pl.szelag.gym.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("UserControlTest - test for contextLoads() method")
    void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(userService).isNotNull();
    }
}