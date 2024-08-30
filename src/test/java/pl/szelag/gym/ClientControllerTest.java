package pl.szelag.gym;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import pl.szelag.gym.client.ClientController;
import pl.szelag.gym.client.ClientService;
import pl.szelag.gym.exception.BadRequestException;
import pl.szelag.gym.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
class ClientControllerTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientController clientController;

    @Test
    @DisplayName("ClientControlTest - test for contextLoads() method")
    void contextLoads() {
        assertThat(clientController).isNotNull();
        assertThat(clientService).isNotNull();
    }

    @Test
    @DisplayName("ClientControlTest - test for BadRequestException")
    void testExpectedBadRequestExceptionIsThrown() {
        BadRequestException badRequestException =
                Assertions.assertThrowsExactly(BadRequestException.class, () -> {
                    throw new BadRequestException("Bad Request");
                });

        assertEquals("Bad Request", badRequestException.getMessage());
        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            throw new BadRequestException("Bad Request");
        });
    }

    @Test
    @DisplayName("ClientControlTest - test for NotFoundException")
    void testExpectedNotFoundExceptionIsThrown() {
        NotFoundException notFoundException =
                Assertions.assertThrowsExactly(NotFoundException.class, () -> {
                    throw new NotFoundException("Not Found");
                });

        assertEquals("Not Found", notFoundException.getMessage());
        Assertions.assertThrowsExactly(NotFoundException.class, () -> {
            throw new NotFoundException("Not Found");
        });
    }

}