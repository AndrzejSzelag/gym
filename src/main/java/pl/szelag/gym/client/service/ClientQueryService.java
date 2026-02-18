package pl.szelag.gym.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.mapper.ClientMapper;
import pl.szelag.gym.client.repository.ClientRepository;
import pl.szelag.gym.common.exception.ResourceNotFoundException;

/** Read-only service for Client aggregate querying and pagination. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ClientQueryService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    /** @param params pagination and filtering criteria paginated client summaries */
    public Page<ClientDtoSummary> getClientsPage(PaginationParams params) {
        String keyword = params.getKeyword();
        long totalItems = (keyword == null || keyword.isBlank())
                ? clientRepository.count() : clientRepository.countByKeyword(keyword);

        int totalPages = (int) Math.ceil((double) totalItems / params.getPageSize());
        if (params.getPageNo() > totalPages && totalPages > 0) {
            params.setPageNo(totalPages);
        }

        return fetchSummariesFromRepository(params);
    }

    /** @param id client identifier mapped ClientDto @throws ResourceNotFoundException if missing */
    public ClientDto getClientDto(Long id) {
        return mapToDtoOrThrow(findEntityByIdOrThrow(id));
    }

    /** @param id client identifier Client entity @throws ResourceNotFoundException if missing */
    public Client findEntityById(Long id) {
        return findEntityByIdOrThrow(id);
    }

    /** @param id identifier found entity @throws ResourceNotFoundException if null or missing */
    private Client findEntityByIdOrThrow(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.client.not.found"));
    }

    /** @param client entity to map mapped DTO */
    private ClientDto mapToDtoOrThrow(Client client) {
        return clientMapper.mapToDto(client);
    }

    /** @param params sorting and paging data page of summaries from repository */
    private Page<ClientDtoSummary> fetchSummariesFromRepository(PaginationParams params) {
        Sort sort = Sort.by(Sort.Direction.fromString(params.getSortDirection()), params.getSortField());
        Pageable pageable = PageRequest.of(params.getPageNo() - 1, params.getPageSize(), sort);

        String kw = params.getKeyword();
        return (kw == null || kw.isBlank())
                ? clientRepository.findAllSummaries(pageable) : clientRepository.searchSummaries(kw, pageable);
    }
}