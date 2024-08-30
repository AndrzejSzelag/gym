package pl.szelag.gym.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {

    public static final String CLIENT_NOT_EXIST = "Client not exist!";

    private final ClientRepository clientRepository;

    public List<Client> getClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .filter(Objects::nonNull)
                .toList();
    }

    public Page<Client> getClientsWithPaginationAndSorting(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return clientRepository.findAll(pageable);
    }

    public List<Client> getClientsWithSearch(String keyword) {
        if (keyword != null) {
            return clientRepository.search(keyword);
        }
        return clientRepository.findAll();
    }

    public Client getClient(final Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new NotFoundException(CLIENT_NOT_EXIST));
    }

    public Client getClient(final String email) {
        return clientRepository.findByEmail(email);
    }

    public void save(final Client client) {
        clientRepository.save(client);
    }

    public Client edit(final Long id, final Client client) {
        Client clientFromDb = getClientFromDb(id);
        checkIsClientExist(id);
        saveEditedClientData(client, clientFromDb);
        clientRepository.save(clientFromDb);
        return clientFromDb;
    }

    private Client getClientFromDb(final Long id) {
        return getClient(id);
    }

    private static void saveEditedClientData(Client newClientData, Client oldClientData) {
        oldClientData.setFirstName(newClientData.getFirstName());
        oldClientData.setLastName(newClientData.getLastName());
        oldClientData.setEmail(newClientData.getEmail());
        oldClientData.setPhone(newClientData.getPhone());
        oldClientData.getAddress().setStreet(newClientData.getAddress().getStreet());
        oldClientData.getAddress().setStreetNumber(newClientData.getAddress().getStreetNumber());
        oldClientData.getAddress().setHomeNumber(newClientData.getAddress().getHomeNumber());
        oldClientData.getAddress().setPostCode(newClientData.getAddress().getPostCode());
        oldClientData.getAddress().setCity(newClientData.getAddress().getCity());
    }

    private void checkIsClientExist(final Long id) throws NotFoundException {
        Optional<Client> client = clientRepository.findById(id);
        if (!client.isPresent()) {
            throw new NotFoundException(CLIENT_NOT_EXIST);
        }
    }

    public Client renew(final Long id, final Client client) {
        Client clientFromDb = getClientFromDb(id);
        checkIsClientExist(id);
        saveRenewedPassClient(client, clientFromDb);
        clientRepository.save(clientFromDb);
        return clientFromDb;
    }

    private static void saveRenewedPassClient(Client newClientPass, Client oldClientPass) {
        oldClientPass.setExpirationDate(newClientPass.getExpirationDate().plusDays(30));
    }

    public void delete(final Long id) {
        checkIsClientExist(id);
        clientRepository.delete(getClient(id));
    }
}