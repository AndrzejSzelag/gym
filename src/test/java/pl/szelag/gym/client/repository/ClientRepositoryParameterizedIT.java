package pl.szelag.gym.client.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.factory.ClientFactory;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parameterized integration tests for Client and Address persistence.
 * Checks if Address entity is correctly created or skipped based on DTO content.
 */
class ClientRepositoryParameterizedIT extends BaseIT {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAllInBatch();
    }

    static Stream<TestCase> clientScenarios() {
        return Stream.of(
                // Full address provided
                new TestCase(

                        "Andrzej",
                        "Szelag",
                        "andrzej.szelag@gym.pl",
                        "123456789",
                        new AddressDto(
                                "Witosa",
                                "10",
                                "5",
                                "62-510",
                                "Konin"),
                        true
                ),
                // Empty address provided (should be ignored)
                new TestCase(
                        "Jan",
                        "Nowak",
                        "jan.empty@gym.pl",
                        "987654321",
                        new AddressDto(),
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("clientScenarios")
    void shouldPersistClientAccordingToAddressPresence(TestCase tc) {
        // GIVEN
        ClientDto dto = ClientDto.builder()
                .firstName(tc.firstName)
                .lastName(tc.lastName)
                .email(tc.email)
                .phone(tc.phone)
                .address(tc.address)
                .build();

        Client client = ClientFactory.fromRegistration(dto);

        // WHEN
        Client saved = clientRepository.save(client);
        Optional<Client> fetchedOpt = clientRepository.findById(saved.getId());

        // THEN
        assertThat(fetchedOpt).isPresent();
        Client fetched = fetchedOpt.get();
        assertThat(fetched.getFirstName()).isEqualTo(tc.firstName);

        if (tc.shouldHaveAddress) {
            assertThat(fetched.getAddress())
                    .as("Address should be persisted for test case: %s", tc.email)
                    .isNotNull();
            assertThat(fetched.getAddress().getStreet()).isEqualTo(tc.address.getStreet());
            assertThat(fetched.getAddress().getCity()).isEqualTo(tc.address.getCity());
        } else {
            assertThat(fetched.getAddress())
                    .as("Address should be null for test case: %s", tc.email)
                    .isNull();
        }
    }

    private record TestCase(
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressDto address,
            boolean shouldHaveAddress
    ) {
        @Override
        public String toString() {
            return email + " (shouldHaveAddress=" + shouldHaveAddress + ")";
        }
    }
}