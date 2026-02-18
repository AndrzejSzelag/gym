package pl.szelag.gym.client.factory;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class ClientFactoryTest {

    @Test
    void shouldCreateClientFromRegistrationDtoWithoutAddress() {
        // GIVEN
        ClientDto dto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .phone("123456789")
                .build();

        // WHEN
        Client client = ClientFactory.fromRegistration(dto);

        // THEN
        assertThat(client).isNotNull();
        assertThat(client.getFirstName()).isEqualTo("Andrzej");
        assertThat(client.getEmail()).isEqualTo("andrzej.szelag@gym.pl");
        assertThat(client.getRegistrationDate()).isEqualTo(LocalDate.now());
        assertThat(client.getAddress()).isNull();
    }

    @Test
    void shouldCreateClientWithAddressWhenAddressIsPresentInDto() {
        // GIVEN
        AddressDto addressDto = AddressDto.builder()
                .city(" Konin ")
                .street(" Main St ")
                .build();

        ClientDto dto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .phone("123456789")
                .address(addressDto)
                .build();

        // WHEN
        Client client = ClientFactory.fromRegistration(dto);

        // THEN
        assertThat(client.getAddress()).isNotNull();
        assertThat(client.getAddress().getCity()).isEqualTo("Konin");
        assertThat(client.getAddress().getStreet()).isEqualTo("Main St");
    }

    @Test
    void shouldIgnoreEmptyAddressFromDto() {
        // GIVEN
        AddressDto emptyAddress = AddressDto.builder().build();

        ClientDto dto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .phone("123456789")
                .address(emptyAddress)
                .build();

        // WHEN
        Client client = ClientFactory.fromRegistration(dto);

        // THEN
        assertThat(client.getAddress()).isNull();
    }

    @Test
    void shouldThrowExceptionWhenDtoIsNull() {
        // WHEN & THEN
        assertThatThrownBy(() -> ClientFactory.fromRegistration(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ClientDto cannot be null.");
    }

    @Test
    void shouldCreateClientFromRegistrationDtoWithAddress() {
        // GIVEN
        AddressDto addressDto = AddressDto.builder()
                .street("Witosa")
                .streetNumber("10")
                .city("Konin")
                .build();

        ClientDto dto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .phone("123456789")
                .address(addressDto)
                .build();

        // WHEN
        Client client = ClientFactory.fromRegistration(dto);

        // THEN
        assertThat(client).isNotNull();
        assertThat(client.getFirstName()).isEqualTo("Andrzej");
        assertThat(client.getAddress()).isNotNull();
        assertThat(client.getAddress().getCity()).isEqualTo("Konin");
    }

}