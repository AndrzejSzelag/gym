package pl.szelag.gym.client.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressDtoTest {

    @Test
    void shouldCreateEmptyAddress() {
        // WHEN
        AddressDto address = AddressDto.empty();

        // THEN
        assertThat(address.getStreet()).isEmpty();
        assertThat(address.getStreetNumber()).isEmpty();
        assertThat(address.getHomeNumber()).isEmpty();
        assertThat(address.getPostCode()).isEmpty();
        assertThat(address.getCity()).isEmpty();
    }

    @Test
    void shouldSetAndGetAddressFields() {
        // GIVEN
        AddressDto address = new AddressDto();

        // WHEN
        address.setCity("Konin");
        address.setStreet("Witosa");
        address.setPostCode("62-510");

        // THEN
        assertThat(address.getCity()).isEqualTo("Konin");
        assertThat(address.getStreet()).isEqualTo("Witosa");
        assertThat(address.getPostCode()).isEqualTo("62-510");
    }
}