package pl.szelag.gym.client.factory;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ClientFactoryParameterizedTest {

    static Stream<TestCase> provideClientScenarios() {
        return Stream.of(
                new TestCase("Andrzej", "Szelag", "andrzej@gym.pl", "123456789", null, false),
                new TestCase("Jan", "Nowak", "jan@gym.pl", "987654321", new AddressDto(), false),
                new TestCase("Marta", "Kowalska", "marta@gym.pl", "111222333",
                        AddressDto.builder().street(" Main St ").city(" Konin ").build(), true),
                new TestCase("Adam", "Nowak", "adam@gym.pl", "444555666",
                        AddressDto.builder().street("Witosa").streetNumber("10").city("Konin").build(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideClientScenarios")
    void shouldCreateClientWithCorrectAddressHandling(TestCase tc) {
        // GIVEN
        ClientDto dto = ClientDto.builder()
                .firstName(tc.firstName)
                .lastName(tc.lastName)
                .email(tc.email)
                .phone(tc.phone)
                .address(tc.address)
                .build();

        // WHEN
        Client client = ClientFactory.fromRegistration(dto);

        // THEN
        assertThat(client).isNotNull();
        assertThat(client.getFirstName()).isEqualTo(tc.firstName);
        assertThat(client.getEmail()).isEqualTo(tc.email);

        if (tc.addressExpected) {
            assertThat(client.getAddress()).isNotNull();
            // optional: check sanitization
            if (tc.address != null) {
                assertThat(client.getAddress().getStreet())
                        .isEqualTo(tc.address.getStreet().trim());
                assertThat(client.getAddress().getCity())
                        .isEqualTo(tc.address.getCity().trim());
            }
        } else {
            assertThat(client.getAddress()).isNull();
        }
    }

    private record TestCase(
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressDto address,
            boolean addressExpected
    ) {}
}
