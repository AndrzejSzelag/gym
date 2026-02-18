package pl.szelag.gym.client.entity;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.common.api.AddressProvider;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ClientUpdateCommandTest {

    @Test
    void shouldCreateRecordAndAccessData() {
        // GIVEN
        String firstName = "Andrzej";
        String lastName = "Szelag";
        String email = "andrzej.szelag@gym.pl";
        String phone = "123456789";
        LocalDate expirationDate = LocalDate.now().plusDays(30);
        AddressProvider address = mock(AddressProvider.class);

        // WHEN
        ClientUpdateCommand command = new ClientUpdateCommand(
                firstName, lastName, email, phone, expirationDate, address
        );

        // THEN
        assertThat(command.firstName()).isEqualTo(firstName);
        assertThat(command.lastName()).isEqualTo(lastName);
        assertThat(command.email()).isEqualTo(email);
        assertThat(command.phone()).isEqualTo(phone);
        assertThat(command.expirationDate()).isEqualTo(expirationDate);
        assertThat(command.address()).isEqualTo(address);
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {
        // GIVEN
        LocalDate date = LocalDate.now();
        ClientUpdateCommand command1 = new ClientUpdateCommand("A", "B", "c@d.pl", "1", date, null);
        ClientUpdateCommand command2 = new ClientUpdateCommand("A", "B", "c@d.pl", "1", date, null);

        // WHEN & THEN
        // Verify that both objects are equal and share the same hash code in a single chain
        assertThat(command1)
                .isEqualTo(command2)
                .hasSameHashCodeAs(command2);
    }

    @Test
    void shouldVerifyToString() {
        // GIVEN
        ClientUpdateCommand command = new ClientUpdateCommand("A", "B", "c@d.pl", "1", null, null);

        // WHEN
        String result = command.toString();

        // THEN
        assertThat(result).contains("firstName=A", "lastName=B", "email=c@d.pl");
    }
}