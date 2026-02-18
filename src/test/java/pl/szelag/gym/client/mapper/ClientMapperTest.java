package pl.szelag.gym.client.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Test
    void shouldMapToNewEntity() {
        // GIVEN – DTO with address and expiration
        AddressDto addressDto = AddressDto.builder()
                .city("Konin")
                .street("Witosa")
                .streetNumber("10")
                .postCode("62-510")
                .build();

        ClientDto dto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .phone("123456789")
                .address(addressDto)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();

        // WHEN – mapping DTO to new entity
        Client entity = mapper.mapToNewEntity(dto);

        // THEN – assert that entity fields were mapped correctly
        assertThat(entity.getFirstName()).isEqualTo("Andrzej");
        assertThat(entity.getAddress()).isNotNull();
        assertThat(entity.getAddress().getCity()).isEqualTo("Konin");
        assertThat(entity.getExpirationDate()).isNotNull();
    }

    @Test
    void shouldUpdateEntityFromDto() {
        // GIVEN – existing entity and update DTO
        Client entity = new Client(
                "Old",
                "Name",
                "old@gym.pl",
                "000",
                LocalDate.now()
        );

        ClientDto updateDto = ClientDto.builder()
                .firstName("New")
                .lastName("Name")
                .email("new@gym.pl")
                .phone("999")
                .address(AddressDto.builder()
                        .city("New City")
                        .street("Nowa")
                        .streetNumber("5")
                        .build())
                .build();

        // WHEN – updating entity using mapper
        mapper.updateEntityFromDto(updateDto, entity);

        // THEN – assert that entity was updated correctly
        assertThat(entity.getFirstName()).isEqualTo("New");
        assertThat(entity.getEmail()).isEqualTo("new@gym.pl");
        assertThat(entity.getPhone()).isEqualTo("999");
        assertThat(entity.getAddress()).isNotNull();
        assertThat(entity.getAddress().getCity()).isEqualTo("New City");
        assertThat(entity.getAddress().getStreet()).isEqualTo("Nowa");
        assertThat(entity.getAddress().getStreetNumber()).isEqualTo("5");
    }
}