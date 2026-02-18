package pl.szelag.gym.client.factory;

import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Address;
import pl.szelag.gym.client.entity.Client;

import java.time.LocalDate;
import java.util.Objects;

/** Factory for creating Client aggregates from various data sources. */
public final class ClientFactory {

    private ClientFactory() {}

    /** @param dto source data new Client aggregate with optional address and registration date */
    public static Client fromRegistration(ClientDto dto) {
        Objects.requireNonNull(dto, "ClientDto cannot be null.");

        Client client = new Client(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getPhone(),
                dto.getRegistrationDate() != null ? dto.getRegistrationDate() : LocalDate.now()
        );

        if (dto.getAddress() != null) {
            Address address = AddressFactory.fromProvider(dto.getAddress());
            if (address != null) {
                client.setOrUpdateAddress(address);
            }
        }

        return client;
    }
}