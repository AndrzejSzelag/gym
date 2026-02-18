package pl.szelag.gym.client.dto;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.common.api.AddressProvider;
import pl.szelag.gym.common.api.MembershipStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientLogicTest {

    private static class TestClientLogic implements ClientLogic {
        private String firstName;
        private String lastName;
        private LocalDate expirationDate;
        private AddressProvider address;

        @Override public String getFirstName() { return firstName; }
        @Override public String getLastName() { return lastName; }
        @Override public LocalDate getExpirationDate() { return expirationDate; }
        @Override public AddressProvider getAddress() { return address; }
    }

    @Test
    void shouldReturnFormattedFullName() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.firstName = " Andrzej ";
        client.lastName = " Szelag ";

        // WHEN
        String fullName = client.getFullName();

        // THEN
        assertThat(fullName).isEqualTo("Andrzej Szelag");
    }

    @Test
    void shouldHandleNullNamesInFullName() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.firstName = null;
        client.lastName = "Szelag";

        // WHEN
        String fullName = client.getFullName();

        // THEN
        assertThat(fullName).isEqualTo("Szelag");
    }

    @Test
    void shouldReturnNoAddressWhenAddressIsNull() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.address = null;

        // WHEN
        String fullAddress = client.getFullAddress();

        // THEN
        assertThat(fullAddress).isEqualTo("No address");
    }

    @Test
    void shouldReturnIncompleteAddressWhenFormattedIsBlank() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        AddressProvider address = mock(AddressProvider.class);
        when(address.getFullAddress()).thenReturn("  ");
        client.address = address;

        // WHEN
        String fullAddress = client.getFullAddress();

        // THEN
        assertThat(fullAddress).isEqualTo("Incomplete address");
    }

    @Test
    void shouldReturnNotAssignedWhenExpirationIsNull() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.expirationDate = null;

        // WHEN
        MembershipStatus status = client.getMembershipStatus();

        // THEN
        assertThat(status).isEqualTo(MembershipStatus.NOT_ASSIGNED);
    }

    @Test
    void shouldReturnActiveWhenExpirationIsInFuture() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.expirationDate = LocalDate.now().plusDays(1);

        // WHEN
        MembershipStatus status = client.getMembershipStatus();

        // THEN
        assertThat(status).isEqualTo(MembershipStatus.ACTIVE);
    }

    @Test
    void shouldReturnExpiredWhenExpirationInPast() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.expirationDate = LocalDate.now().minusDays(1);

        // WHEN
        MembershipStatus status = client.getMembershipStatus();

        // THEN
        assertThat(status).isEqualTo(MembershipStatus.EXPIRED);
    }

    @Test
    void shouldCalculateDaysUntilOrSinceExpiration() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.expirationDate = LocalDate.now().plusDays(10);

        // WHEN
        Long days = client.daysUntilOrSinceExpiration();

        // THEN
        assertThat(days).isEqualTo(10);
    }

    @Test
    void shouldReturnNullDaysWhenExpirationIsNull() {
        // GIVEN
        TestClientLogic client = new TestClientLogic();
        client.expirationDate = null;

        // WHEN
        Long days = client.daysUntilOrSinceExpiration();

        // THEN
        assertThat(days).isNull();
    }
}