package pl.szelag.gym.client.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.factory.ClientFactory;
import pl.szelag.gym.client.mapper.ClientMapper;
import pl.szelag.gym.client.repository.ClientRepository;
import pl.szelag.gym.common.exception.DuplicateResourceException;

/** * Write-only operations for Client aggregate management. */
@Slf4j
@Validated
@Service
@Transactional
@RequiredArgsConstructor
public class ClientCommandService {

    /** Default number of days for membership extension. */
    private static final int MEMBERSHIP_EXTENSION_DAYS = 30;

    /** Repository for client persistence. */
    private final ClientRepository clientRepository;
    /** Mapper for DTO-entity conversions. */
    private final ClientMapper clientMapper;
    /** Service for retrieving client entities. */
    private final ClientQueryService clientQueryService;

    /**
     * Creates a new client in the system.
     *
     * @param clientDto registration data
     * @return persisted client DTO
     * @throws DuplicateResourceException if email taken
     */
    public ClientDto createClient(ClientDto clientDto) {
        log.info("Creating client: {}", clientDto.getEmail());
        validateEmailDoesNotExist(clientDto.getEmail());
        Client client = ClientFactory.fromRegistration(clientDto);
        return clientMapper.mapToDto(clientRepository.save(client));
    }

    /**
     * Updates an existing client's data.
     *
     * @param id  client identifier
     * @param dto update data
     * @throws EntityNotFoundException    if missing
     * @throws DuplicateResourceException on email conflict
     */
    public void updateClient(Long id, ClientDto dto) {
        log.info("Updating client ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + id));
        validateEmailUniqueness(dto.getEmail(), id);
        client.updateClient(dto.toCommand());
        clientRepository.save(client);
    }

    /**
     * Extends client membership by 30 days.
     *
     * @param id client identifier to extend membership
     * @throws EntityNotFoundException if missing
     */
    public void extendMembership(Long id) {
        log.info("Extending membership for client ID: {}", id);
        Client client = clientQueryService.findEntityById(id);
        client.extendMembership(MEMBERSHIP_EXTENSION_DAYS);
        clientRepository.save(client);
    }

    /**
     * Permanently removes a client from the system.
     *
     * @param id client identifier for removal
     * @throws EntityNotFoundException if missing
     */
    public void deleteClient(Long id) {
        log.info("Deleting client ID: {}", id);
        Client client = clientQueryService.findEntityById(id);
        clientRepository.delete(client);
    }

    /**
     * Verifies if email is not already used in the system.
     *
     * @param email address to verify
     * @throws DuplicateResourceException if already exists
     */
    private void validateEmailDoesNotExist(String email) {
        clientRepository.findByEmail(email).ifPresent(e -> {
            throw new DuplicateResourceException("CLIENT", "error.duplicateClientEmail", email);
        });
    }

    /**
     * Verifies if email is unique, excluding the specified client ID.
     *
     * @param email     address to verify
     * @param excludeId client ID to ignore
     * @throws DuplicateResourceException if exists
     */
    private void validateEmailUniqueness(String email, Long excludeId) {
        clientRepository.findByEmailAndIdNot(email, excludeId).ifPresent(e -> {
            throw new DuplicateResourceException("CLIENT", "error.duplicateClientEmail", email);
        });
    }
}