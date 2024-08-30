package pl.szelag.gym;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.szelag.gym.client.Address;
import pl.szelag.gym.client.Client;
import pl.szelag.gym.client.ClientRepository;
import pl.szelag.gym.client.ClientService;
import pl.szelag.gym.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client clientBuilder1L;

    @BeforeEach
    void init() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);

        clientBuilder1L = Client.builder()
                .id(1L)
                .firstName("Andrzej")
                .lastName("Szelag")
                .phone("123456789")
                .email("andrzejszelag@ea.pl")
                .registrationDate(LocalDate.parse("2024-09-23"))
                .expirationDate(LocalDate.parse("2024-09-23"))
                .address(Address.builder().build()).build();
    }

    @Test
    @DisplayName("Test for getClients() method [POSITIVE SCENARIO]")
    void givenClientList_whenGetClients_thenReturnClientList() {

        // given
        Client clientBuilder2L = Client.builder()
                .id(2L)
                .firstName("Nikodem")
                .lastName("Szelag")
                .phone("987654321")
                .email("nikodemszelag@ea.pl")
                .registrationDate(LocalDate.parse("2024-09-23"))
                .expirationDate(LocalDate.parse("2024-09-23"))
                .address(Address.builder().build()).build();
        given(clientRepository.findAll()).willReturn(List.of(clientBuilder1L, clientBuilder2L));

        // when
        List<Client> clientList = clientService.getClients();

        // then
        Assertions.assertNotNull(clientList);
        assertThat(clientList).hasSize(2);
        verify(clientRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test for getClientsWithPaginationAndSorting() method")
    void givenClientList_whenGetClientsWithPaginationAndSorting_thenReturnClientList() {

        // given
        Page clients = mock(Page.class);

        // when
        when(clientRepository.findAll(any(PageRequest.class))).thenReturn(clients);
        clientService.getClientsWithPaginationAndSorting(1, 7, "name", "asc");

        // then
        verify(clientRepository, Mockito.times(1)).findAll(any(PageRequest.class));
        verify(clientRepository).findAll(any(PageRequest.class));
        verifyNoMoreInteractions(clientRepository);
    }

    @Test
    @DisplayName("Test for getClientsWithSearch() method")
    void givenClientList_whenGetClientsWithSearch_thenReturnClientList() {

        // given
        String keyword = "Szelag";

        // when
        clientService.getClientsWithSearch(keyword);

        // then
        verify(clientRepository, Mockito.times(1)).search("Szelag");
        verify(clientRepository).search("Szelag");
        verifyNoMoreInteractions(clientRepository);
    }

    @Test
    @DisplayName("Test for getClients() method [NEGATIVE SCENARIO]")
    void givenEmptyClientList_whenGetClients_thenReturnEmptyClientList() {

        // given
        given(clientRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<Client> clientList = clientService.getClients();

        // then
        assertThat(clientList).isEmpty();
        verify(clientRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test for getClient()_byId method")
    void givenClientById_whenGetClient_thenReturnClient() {

        // given
        given(clientRepository.findById(1L)).willReturn(Optional.of(clientBuilder1L));

        // when
        Client savedClient = clientService.getClient(clientBuilder1L.getId());

        // then
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isEqualTo(1L);
        verify(clientRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test for getClient()_byEmail method")
    void givenClientByEmail_whenGetClient_thenReturnClient() {

        // given
        given(clientRepository.findByEmail("andrzejszelag@ea.pl")).willReturn(clientBuilder1L);

        // when
        Client savedClient = clientService.getClient(clientBuilder1L.getEmail());

        // then
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getEmail()).isEqualTo("andrzejszelag@ea.pl");
        verify(clientRepository, Mockito.times(1)).findByEmail("andrzejszelag@ea.pl");
    }

    @Test
    @DisplayName("Test for save() method with EmptyClient")
    void givenClientObject_whenSave_thenReturnEmptyClient() {

        // given
        Client clientBuilderEmptyClient = Client.builder().address(Address.builder().build()).build();

        // when
        clientService.save(clientBuilderEmptyClient);
        clientRepository.save(clientBuilderEmptyClient);

        // then
        assertThat(clientBuilderEmptyClient.getId()).isNull();
    }

    @Test
    @DisplayName("Test for save() method")
    void givenClientObject_whenSave_thenReturnClient() {

        // given
        Client clientBuilder3L = Client.builder()
                .id(3L)
                .firstName("Emilia")
                .lastName("Szelag")
                .phone("543216789")
                .email("emiliaszelag@ea.pl")
                .registrationDate(LocalDate.parse("2024-09-23"))
                .expirationDate(LocalDate.parse("2024-09-23"))
                .address(Address.builder().build()).build();

        // when
        clientService.save(clientBuilder3L);
        clientRepository.save(clientBuilder3L);

        // then
        Assertions.assertNotNull(clientBuilder3L);
        assertThat(clientBuilder3L.getId()).isEqualTo(3);
        verify(clientRepository, Mockito.times(2)).save(clientBuilder3L);
    }

    @Test
    @DisplayName("Test for NotFoundException")
    void testExpectedExceptionIsThrown() {
        NotFoundException notFoundException =
                Assertions.assertThrowsExactly(NotFoundException.class, () -> {
                    throw new NotFoundException("Client not exist!");
                });

        assertEquals("Client not exist!", notFoundException.getMessage());
        Assertions.assertThrowsExactly(NotFoundException.class, () -> {
            throw new NotFoundException("Client not exist!");
        });
    }

    @Test
    @DisplayName("Test for edit() method")
    void givenClient_whenEdit_thenReturnEditedClientData() {

        // given
        given(clientRepository.findById(1L)).willReturn(Optional.of(clientBuilder1L));

        // when
        final Client clientFromDb = clientBuilder1L;

        clientFromDb.setFirstName(clientBuilder1L.getFirstName());
        clientFromDb.setLastName(clientBuilder1L.getLastName());
        clientFromDb.setEmail("aszelag@ea.pl");
        clientFromDb.setPhone("888111555");
        clientFromDb.getAddress().setStreet(clientBuilder1L.getAddress().getStreet());
        clientFromDb.getAddress().setStreetNumber(clientBuilder1L.getAddress().getStreetNumber());
        clientFromDb.getAddress().setHomeNumber(clientBuilder1L.getAddress().getHomeNumber());
        clientFromDb.getAddress().setPostCode(clientBuilder1L.getAddress().getPostCode());
        clientFromDb.getAddress().setCity(clientBuilder1L.getAddress().getCity());

        clientService.edit(clientBuilder1L.getId(), clientFromDb);
        clientRepository.save(clientFromDb);

        // then
        assertThat(clientFromDb.getEmail()).isEqualTo("aszelag@ea.pl");
        assertThat(clientFromDb.getPhone()).isEqualTo("888111555");
        verify(clientRepository, Mockito.times(2)).save(clientFromDb);
    }

    @Test
    @DisplayName("Test for renew() method")
    void givenClient_whenRenew_thenReturnRenewedPassClient() {

        // given
        given(clientRepository.findById(1L)).willReturn(Optional.of(clientBuilder1L));

        // when
        final Client clientFromDb = clientBuilder1L;

        clientFromDb.setExpirationDate(clientBuilder1L.getExpirationDate()); // 2024-09-23

        clientService.renew(clientBuilder1L.getId(), clientFromDb);         // + 30 days
        clientRepository.save(clientFromDb);                                // 2024-10-23

        // then
        assertThat(clientFromDb.getExpirationDate()).isEqualTo("2024-10-23");
        verify(clientRepository, Mockito.times(2)).save(clientFromDb);
    }

    @Test
    @DisplayName("Test for delete() method")
    void givenClient_whenDelete_thenVerifyClient() {

        // given
        given(clientRepository.findById(1L)).willReturn(Optional.of(clientBuilder1L));

        // when
        clientService.delete(clientBuilder1L.getId());
        clientRepository.deleteById(clientBuilder1L.getId());

        // then
        verify(clientRepository).deleteById(clientBuilder1L.getId());
    }
}