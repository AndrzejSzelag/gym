package pl.szelag.gym.client.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ClientDtoTest {

    @Test
    void shouldBuildEmptyClientDto() {
        // WHEN
        ClientDto dto = ClientDto.empty();

        // THEN
        assertThat(dto.getFirstName()).isEmpty();
        assertThat(dto.getLastName()).isEmpty();
        assertThat(dto.getEmail()).isEmpty();
        assertThat(dto.getPhone()).isEmpty();
        assertThat(dto.getAddress()).isNotNull();
    }

    @Test
    void shouldMapToClientUpdateCommand() {
        // GIVEN
        AddressDto address = AddressDto.builder()
                .city("Konin")
                .build();

        ClientDto dto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .phone("123456789")
                .expirationDate(LocalDate.now().plusDays(5))
                .address(address)
                .build();

        // WHEN
        var command = dto.toCommand();

        // THEN
        assertThat(command.firstName()).isEqualTo("Andrzej");
        assertThat(command.lastName()).isEqualTo("Szelag");
        assertThat(command.email()).isEqualTo("andrzej.szelag@gym.pl");
        assertThat(command.phone()).isEqualTo("123456789");
        assertThat(command.expirationDate()).isEqualTo(dto.getExpirationDate());
        assertThat(command.address()).isSameAs(address);
    }

    @Test
    void shouldCalculateDaysUntilExpiration() {
        // GIVEN
        LocalDate today = LocalDate.now();
        ClientDto dto = ClientDto.builder()
                .expirationDate(today.plusDays(10))
                .build();

        // WHEN
        long days = dto.daysUntilOrSinceExpiration();

        // THEN
        assertThat(days).isEqualTo(10);
    }

    @Test
    void shouldDetectActiveAndExpiredSubscription() {
        // GIVEN
        ClientDto active = ClientDto.builder()
                .expirationDate(LocalDate.now().plusDays(1))
                .build();

        ClientDto expired = ClientDto.builder()
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        // THEN
        assertThat(active.hasActiveSubscription()).isTrue();
        assertThat(expired.hasActiveSubscription()).isFalse();
    }

    @Test
    void shouldReturnNoAddressWhenAddressIsNull() {
        // GIVEN
        ClientDto dto = ClientDto.builder()
                .address(null)
                .build();

        // WHEN
        String address = dto.getFullAddress();

        // THEN
        assertThat(address).isEqualTo("No address");
    }
}