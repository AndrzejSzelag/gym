package pl.szelag.gym.client.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.factory.ClientFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Client search and summary projection functionality.
 * Verifies keyword-based searching and conditional address mapping in summaries.
 */
class ClientRepositorySearchIT extends BaseIT {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUpClients() {
        // GIVEN
        clientRepository.deleteAllInBatch();

        // Client 1: Full address provided
        ClientDto dto1 = ClientDto.builder()
                .firstName("Anna")
                .lastName("Kowalska")
                .email("anna@gym.pl")
                .phone("123456789")
                .address(AddressDto.builder()
                        .street("Witosa")
                        .city("Konin")
                        .postCode("62-510")
                        .streetNumber("10")
                        .homeNumber("5")
                        .build())
                .build();

        // Client 2: Empty address provided (should be ignored by factory/persistence)
        ClientDto dto2 = ClientDto.builder()
                .firstName("Jan")
                .lastName("Nowak")
                .email("jan@gym.pl")
                .phone("987654321")
                .address(new AddressDto())
                .build();

        clientRepository.save(ClientFactory.fromRegistration(dto1));
        clientRepository.save(ClientFactory.fromRegistration(dto2));
    }

    @Test
    void shouldReturnAllSummariesWithAddressIncluded() {
        // WHEN
        Page<ClientDtoSummary> page = clientRepository.findAllSummaries(PageRequest.of(0, 10));

        // THEN
        List<ClientDtoSummary> clients = page.getContent();
        assertThat(clients).hasSize(2);

        // Verify Anna has her address mapped correctly
        assertThat(clients).filteredOn(c -> c.getFirstName().equals("Anna"))
                .singleElement()
                .satisfies(anna -> {
                    assertThat(anna.getAddress()).isNotNull();
                    assertThat(anna.getAddress().getCity()).isEqualTo("Konin");
                });

        // Verify Jan has no address entity associated
        assertThat(clients).filteredOn(c -> c.getFirstName().equals("Jan"))
                .singleElement()
                .satisfies(jan -> assertThat(jan.getAddress()).isNull());
    }

    @Test
    void shouldSearchByKeywordIgnoringCase() {
        // WHEN
        Page<ClientDtoSummary> page = clientRepository.searchSummaries("kowal", PageRequest.of(0, 10));

        // THEN
        List<ClientDtoSummary> results = page.getContent();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Anna");
    }

    @Test
    void shouldCountByKeyword() {
        // WHEN
        long count = clientRepository.countByKeyword("nowak");

        // THEN
        assertThat(count).isEqualTo(1);
    }
}