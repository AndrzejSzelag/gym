package pl.szelag.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.szelag.gym.client.Address;
import pl.szelag.gym.client.Client;
import pl.szelag.gym.client.ClientRepository;
import pl.szelag.gym.client.ClientService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceAndClientRepositoryTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
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
                .address(Address.builder().build()).build();
    }

    @BeforeEach
    void setup() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientBuilder1L));
        when(clientRepository.findByEmail("andrzejszelag@ea.pl")).thenReturn(clientBuilder1L);
    }

    @Test
    @DisplayName("ClientServiceAndClientRepositoryIT - test for testGetMockClientService() method")
    void testGetMockClientService() {
        assertEquals(1L, clientService.getClient(1L).getId());
        assertEquals("andrzejszelag@ea.pl", clientService.getClient("andrzejszelag@ea.pl").getEmail());
    }
}