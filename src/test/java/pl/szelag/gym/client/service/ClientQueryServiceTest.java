package pl.szelag.gym.client.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.mapper.ClientMapper;
import pl.szelag.gym.client.repository.ClientRepository;
import pl.szelag.gym.common.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientQueryService clientQueryService;

    // --- GET CLIENT DTO ---

    @Test
    void shouldReturnClientDtoWhenFound() {
        Long id = 1L;
        Client client = mock(Client.class);
        ClientDto dto = ClientDto.builder()
                .email("andrzej.szelag@gym.pl")
                .firstName("Andrzej")
                .lastName("Szelag")
                .build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(clientMapper.mapToDto(client)).thenReturn(dto);

        ClientDto result = clientQueryService.getClientDto(id);

        assertThat(result.getEmail()).isEqualTo("andrzej.szelag@gym.pl");
        assertThat(result.getLastName()).isEqualTo("Szelag");
    }

    @Test
    void shouldThrowExceptionWhenClientDtoNotFound() {
        Long id = 99L;
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientQueryService.getClientDto(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("error.client.not.found");
    }

    // --- GET CLIENT ENTITY ---

    @Test
    void shouldReturnClientEntityWhenFound() {
        Long id = 2L;
        Client client = mock(Client.class);
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));

        Client result = clientQueryService.findEntityById(id);

        assertThat(result).isEqualTo(client);
    }

    @Test
    void shouldThrowExceptionWhenClientEntityNotFound() {
        Long id = 99L;
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientQueryService.findEntityById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("error.client.not.found");
    }

    // --- PAGINATION ---

    @Test
    void shouldAdjustPageNumberWhenOutOfBounds() {
        PaginationParams params = new PaginationParams();
        params.setPageNo(10);
        params.setPageSize(5);
        params.setSortField("lastName");
        params.setSortDirection("asc");

        when(clientRepository.count()).thenReturn(12L); // 3 pages total
        when(clientRepository.findAllSummaries(any(Pageable.class))).thenReturn(Page.empty());

        clientQueryService.getClientsPage(params);

        assertThat(params.getPageNo()).isEqualTo(3);
    }

    @Test
    void shouldFetchSummariesWithoutKeyword() {
        PaginationParams params = new PaginationParams();
        params.setPageNo(1);
        params.setPageSize(5);
        params.setSortField("email");
        params.setSortDirection("desc");

        List<ClientDtoSummary> summaries = List.of(mock(ClientDtoSummary.class));
        Page<ClientDtoSummary> page = new PageImpl<>(summaries);

        when(clientRepository.count()).thenReturn(1L);
        when(clientRepository.findAllSummaries(any(Pageable.class))).thenReturn(page);

        Page<ClientDtoSummary> result = clientQueryService.getClientsPage(params);

        assertThat(result.getContent()).hasSize(1);
        verify(clientRepository).findAllSummaries(any(Pageable.class));
        verify(clientRepository, never()).searchSummaries(any(), any());
    }

    @Test
    void shouldFetchSummariesWithKeyword() {
        PaginationParams params = new PaginationParams();
        params.setKeyword("Szelag");
        params.setPageNo(1);
        params.setPageSize(10);
        params.setSortField("email");
        params.setSortDirection("desc");

        List<ClientDtoSummary> summaries = List.of(mock(ClientDtoSummary.class));
        Page<ClientDtoSummary> page = new PageImpl<>(summaries);

        when(clientRepository.countByKeyword("Szelag")).thenReturn(1L);
        when(clientRepository.searchSummaries(eq("Szelag"), any(Pageable.class))).thenReturn(page);

        Page<ClientDtoSummary> result = clientQueryService.getClientsPage(params);

        assertThat(result.getContent()).hasSize(1);
        verify(clientRepository).searchSummaries(eq("Szelag"), any(Pageable.class));
        verify(clientRepository, never()).findAllSummaries(any());
    }
}