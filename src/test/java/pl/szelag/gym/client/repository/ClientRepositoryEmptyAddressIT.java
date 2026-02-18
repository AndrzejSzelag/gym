package pl.szelag.gym.client.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.factory.ClientFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Client persistence logic regarding optional components.
 * Verifies that the system handles partial data (like empty addresses) correctly.
 */
class ClientRepositoryEmptyAddressIT extends BaseIT {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldPersistClientWithoutAddressWhenAddressDtoIsEmpty() {
        // GIVEN: A client DTO with an empty address object
        AddressDto emptyAddressDto = AddressDto.builder().build();

        ClientDto dto = ClientDto.builder()
                .firstName("Anna")
                .lastName("Kowalska")
                .email("anna.it@gym.pl")
                .phone("987654321")
                .address(emptyAddressDto)
                .build();

        Client client = ClientFactory.fromRegistration(dto);

        // WHEN: Saving the client to the database
        Client saved = clientRepository.save(client);
        Optional<Client> fetchedOpt = clientRepository.findById(saved.getId());

        // THEN: Client is saved, but address remains null in the database
        assertThat(fetchedOpt).isPresent();
        Client fetched = fetchedOpt.get();

        assertThat(fetched.getFirstName()).isEqualTo("Anna");
        assertThat(fetched.getAddress())
                .as("Address entity should not be created if DTO was empty")
                .isNull();
    }
}