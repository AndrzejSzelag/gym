package pl.szelag.gym.client.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.entity.Client;
import pl.szelag.gym.client.mapper.ClientMapper;
import pl.szelag.gym.client.repository.ClientRepository;
import pl.szelag.gym.common.exception.DuplicateResourceException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientCommandServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private ClientQueryService clientQueryService;

    @InjectMocks
    private ClientCommandService clientCommandService;

    // --- CREATE CLIENT ---

    @Test
    void shouldCreateClientSuccessfully() {
        // GIVEN
        ClientDto dto = createSampleClientDto("andrzej.szelag@gym.pl");
        when(clientRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);
        when(clientMapper.mapToDto(any(Client.class))).thenReturn(dto);

        // WHEN
        clientCommandService.createClient(dto);

        // THEN
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingClientWithDuplicateEmail() {
        // GIVEN
        String email = "andrzej.szelag@gym.pl";
        ClientDto dto = createSampleClientDto(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(mock(Client.class)));

        // WHEN & THEN
        assertThatThrownBy(() -> clientCommandService.createClient(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("error.duplicateClientEmail");

        verify(clientRepository, never()).save(any());
    }

    // --- UPDATE CLIENT ---

    @Test
    void shouldUpdateClientSuccessfully() {
        // GIVEN
        Long id = 1L;
        ClientDto dto = createSampleClientDto("updated@email.com");
        Client existingClient = mock(Client.class);

        when(clientRepository.findById(id)).thenReturn(Optional.of(existingClient));
        when(clientRepository.findByEmailAndIdNot(dto.getEmail(), id)).thenReturn(Optional.empty());

        // WHEN
        clientCommandService.updateClient(id, dto);

        // THEN
        verify(existingClient, times(1)).updateClient(any());
        verify(clientRepository, times(1)).save(existingClient);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        // GIVEN
        Long id = 1L;
        ClientDto dto = createSampleClientDto("new@email.com");
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> clientCommandService.updateClient(id, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingClientWithDuplicateEmail() {
        // GIVEN
        Long id = 1L;
        ClientDto dto = createSampleClientDto("duplicate@email.com");
        Client existingClient = mock(Client.class);
        Client otherClient = mock(Client.class);

        when(clientRepository.findById(id)).thenReturn(Optional.of(existingClient));
        when(clientRepository.findByEmailAndIdNot(dto.getEmail(), id)).thenReturn(Optional.of(otherClient));

        // WHEN & THEN
        assertThatThrownBy(() -> clientCommandService.updateClient(id, dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("error.duplicateClientEmail");

        verify(clientRepository, never()).save(existingClient);
    }

    // --- EXTEND MEMBERSHIP ---

    @Test
    void shouldExtendMembershipSuccessfully() {
        // GIVEN
        Long id = 1L;
        Client client = mock(Client.class);
        when(clientQueryService.findEntityById(id)).thenReturn(client);

        // WHEN
        clientCommandService.extendMembership(id);

        // THEN
        verify(client, times(1)).extendMembership(30);
        verify(clientRepository, times(1)).save(client);
    }

    // --- DELETE CLIENT ---

    @Test
    void shouldDeleteClientSuccessfully() {
        // GIVEN
        Long id = 1L;
        Client client = mock(Client.class);
        when(clientQueryService.findEntityById(id)).thenReturn(client);

        // WHEN
        clientCommandService.deleteClient(id);

        // THEN
        verify(clientRepository, times(1)).delete(client);
    }

    // --- DTO MAPPING CHECK ---

    @Test
    void shouldMapDtoCorrectlyAfterCreate() {
        // GIVEN
        ClientDto dto = createSampleClientDto("newclient@email.com");
        Client clientEntity = mock(Client.class);

        when(clientRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(clientEntity);
        when(clientMapper.mapToDto(clientEntity)).thenReturn(dto);

        // WHEN
        ClientDto result = clientCommandService.createClient(dto);

        // THEN
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(result.getAddress()).isNotNull();
    }

    // --- HELPERS ---

    private ClientDto createSampleClientDto(String email) {
        return ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email(email)
                .address(AddressDto.builder()
                        .street("Witosa").streetNumber("10").postCode("62-510").city("Konin")
                        .build())
                .build();
    }
}