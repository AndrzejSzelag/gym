package pl.szelag.gym.client.entity;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.common.api.AddressProvider;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientTest {

    private static final String TEST_EMAIL = "andrzej.szelag@gym.pl";
    private static final String TEST_FIRST_NAME = "Andrzej";
    private static final String TEST_LAST_NAME = "Szelag";
    private static final String TEST_PHONE = "123456789";

    @Test
    void shouldCreateClientWithDefaultRegistrationDate() {
        // GIVEN
        // Constants used for personal data

        // WHEN
        Client client = new Client(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, null);

        // THEN
        assertThat(client.getRegistrationDate()).isEqualTo(LocalDate.now());
        assertThat(client.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void shouldExtendMembershipFromTodayWhenNoExpirationDateSet() {
        // GIVEN
        Client client = new Client(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, LocalDate.now());

        // WHEN
        client.extendMembership(30);

        // THEN
        assertThat(client.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(30));
    }

    @Test
    void shouldExtendMembershipFromExistingFutureExpirationDate() {
        // GIVEN
        Client client = new Client(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, LocalDate.now());
        client.setMembershipExpiration(LocalDate.now().plusDays(10));

        // WHEN
        client.extendMembership(30);

        // THEN
        assertThat(client.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(40));
    }

    @Test
    void shouldThrowExceptionWhenSettingExpirationDateInPast() {
        // GIVEN
        Client client = new Client(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, LocalDate.now());
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // WHEN & THEN
        assertThatThrownBy(() -> client.setMembershipExpiration(pastDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expiration date cannot be in the past");
    }

    @Test
    void shouldUpdateClientFromCommand() {
        // GIVEN
        Client client = new Client("Old", "Name", "old@gym.pl", "000111222", LocalDate.now());
        ClientUpdateCommand command = new ClientUpdateCommand(
                TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE,
                LocalDate.now().plusDays(5), null
        );

        // WHEN
        client.updateClient(command);

        // THEN
        assertThat(client.getFirstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(client.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(client.getPhone()).isEqualTo(TEST_PHONE);
        assertThat(client.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(5));
    }

    @Test
    void shouldSetNewAddressWhenNoneExists() {
        // GIVEN
        Client client = new Client(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, LocalDate.now());
        AddressProvider provider = mock(AddressProvider.class);
        when(provider.getStreet()).thenReturn("Main St");
        when(provider.getCity()).thenReturn("Konin");

        // WHEN
        client.setOrUpdateAddress(provider);

        // THEN
        assertThat(client.getAddress()).isNotNull();
        assertThat(client.getAddress().getStreet()).isEqualTo("Main St");
    }

    @Test
    void shouldSanitizeAndNormalizeClientProfile() {
        // GIVEN
        Client client = new Client("  " + TEST_FIRST_NAME + "  ", TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, LocalDate.now());

        // WHEN
        client.updateClientProfile("  Jan  ", " Nowak ", " TEST@GYM.PL ", " 987654321 ");

        // THEN
        assertThat(client.getFirstName()).isEqualTo("Jan");
        assertThat(client.getLastName()).isEqualTo("Nowak");
        assertThat(client.getEmail()).isEqualTo("test@gym.pl");
    }

    @Test
    void shouldHandleNullAddressProviderGracefully() {
        // GIVEN
        Client client = new Client(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PHONE, LocalDate.now());

        // WHEN
        client.setOrUpdateAddress(null);

        // THEN
        assertThat(client.getAddress()).isNull();
    }
}