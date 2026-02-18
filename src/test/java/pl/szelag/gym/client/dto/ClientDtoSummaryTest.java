package pl.szelag.gym.client.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ClientDtoSummaryTest {

    @Test
    void shouldReturnFullNameUsingDefaultInterfaceLogic() {
        // GIVEN
        ClientDtoSummary summary = new ClientDtoSummary() {
            @Override public Long getId() { return 1L; }
            @Override public String getFirstName() { return "Andrzej"; }
            @Override public String getLastName() { return "Szelag"; }
            @Override public String getEmail() { return "test@gym.pl"; }
            @Override public String getPhone() { return "123"; }
            @Override public LocalDate getRegistrationDate() { return LocalDate.now(); }
            @Override public LocalDate getExpirationDate() { return LocalDate.now().plusDays(30); }
            @Override public AddressSummary getAddress() { return null; }
        };

        // WHEN
        String fullName = summary.getFullName();

        // THEN
        assertThat(fullName).isEqualTo("Andrzej Szelag");
    }

    @Test
    void shouldHandleMembershipFlagsWhenExpirationDateIsPresent() {
        // GIVEN
        ClientDtoSummary summary = new ClientDtoSummary() {
            @Override public Long getId() { return 1L; }
            @Override public String getFirstName() { return "A"; }
            @Override public String getLastName() { return "B"; }
            @Override public String getEmail() { return null; }
            @Override public String getPhone() { return null; }
            @Override public LocalDate getRegistrationDate() { return null; }
            @Override public LocalDate getExpirationDate() { return LocalDate.now().plusDays(10); }
            @Override public AddressSummary getAddress() { return null; }
        };

        // THEN
        assertThat(summary.hasAssignedSubscription()).isTrue();
        assertThat(summary.hasActiveSubscription()).isTrue();
    }

    @Test
    void shouldReturnNoAddressMessageWhenAddressIsNull() {
        // GIVEN
        ClientDtoSummary summary = new ClientDtoSummary() {
            @Override public Long getId() { return null; }
            @Override public String getFirstName() { return null; }
            @Override public String getLastName() { return null; }
            @Override public String getEmail() { return null; }
            @Override public String getPhone() { return null; }
            @Override public LocalDate getRegistrationDate() { return null; }
            @Override public LocalDate getExpirationDate() { return null; }
            @Override public AddressSummary getAddress() { return null; }
        };

        // WHEN
        String result = summary.getFullAddress();

        // THEN
        assertThat(result).isEqualTo("No address");
    }
}