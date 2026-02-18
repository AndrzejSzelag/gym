package pl.szelag.gym.client.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.client.entity.Client;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ClientRepository.
 * Verifies basic CRUD operations and database constraints.
 */
class ClientRepositoryIT extends BaseIT {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        // Czyścimy wszystko przed każdym testem, żeby nie było "Duplicate key"
        clientRepository.deleteAllInBatch();
    }

    @Test
    void shouldSaveAndFindClient() {
        // GIVEN
        String email = "andrzej.szelag@gym.pl";
        Client client = new Client(
                "Andrzej",
                "Szelag",
                email,
                "123456789",
                LocalDate.now()
        );

        // WHEN
        Client savedClient = clientRepository.save(client);
        Optional<Client> foundClient = clientRepository.findById(savedClient.getId());

        // THEN
        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getFirstName()).isEqualTo("Andrzej");
        assertThat(foundClient.get().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldMaintainEmailUniqueness() {
        // GIVEN
        String email = "unique@gym.pl";
        Client firstClient = new Client(
                "Andrzej",
                "Szelag",
                email,
                "111222333",
                LocalDate.now()
        );
        clientRepository.saveAndFlush(firstClient);

        // WHEN
        Client secondClient = new Client(
                "Piotr",
                "Nowak",
                email,
                "444555666",
                LocalDate.now()
        );

        // THEN: Expecting an exception due to unique constraint on email column
        assertThatThrownBy(() -> {
            clientRepository.saveAndFlush(secondClient);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}