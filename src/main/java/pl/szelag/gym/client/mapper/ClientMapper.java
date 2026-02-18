package pl.szelag.gym.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Address;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.factory.AddressFactory;

/** MapStruct mapper for Client aggregate. Preserves domain invariants by using entity methods. */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ClientMapper {

    /** mapped client DTO from entity */
    ClientDto mapToDto(Client entity);

    /** @param dto source data @param entity target aggregate to update */
    default void updateEntityFromDto(ClientDto dto, @MappingTarget Client entity) {
        if (dto == null || entity == null) return;

        entity.updateClientProfile(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone()
        );

        if (dto.getExpirationDate() != null) {
            entity.setMembershipExpiration(dto.getExpirationDate());
        }

        if (dto.getAddress() != null) {
            Address address = AddressFactory.fromProvider(dto.getAddress());
            if (address != null) {
                entity.setOrUpdateAddress(address);
            }
        }
    }

    /** @param dto source data new client aggregate instance */
    default Client mapToNewEntity(ClientDto dto) {
        if (dto == null) return null;

        Client client = new Client(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getPhone(),
                dto.getRegistrationDate()
        );

        if (dto.getAddress() != null) {
            Address address = AddressFactory.fromProvider(dto.getAddress());
            if (address != null) {
                client.setOrUpdateAddress(address);
            }
        }

        if (dto.getExpirationDate() != null) {
            client.setMembershipExpiration(dto.getExpirationDate());
        }

        return client;
    }
}