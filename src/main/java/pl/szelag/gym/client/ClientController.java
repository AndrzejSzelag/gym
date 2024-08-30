package pl.szelag.gym.client;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.szelag.gym.exception.BadRequestException;
import pl.szelag.gym.exception.NotFoundException;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class ClientController {

    public static final String CLIENTS = "clients";
    public static final String CLIENT = "client";
    public static final String ACTION = "action";
    public static final String REDIRECT_CLIENTS_SUCCESS = "redirect:/clients?success";

    private final ClientService clientService;

    @GetMapping(value = "/clients")
    public String getClients(Model model){
            return findPaginatedAndSortedClients(1, "id", "asc", model);
    }

    @GetMapping("/clients/{pageNo}")
    public String findPaginatedAndSortedClients(@PathVariable (value = "pageNo") int pageNo,
                                                @RequestParam("sortField") String sortField,
                                                @RequestParam("sortDirection") String sortDirection,
                                                Model model) {
        int pageSize = 7;
        try {
            Page<Client> page = clientService.getClientsWithPaginationAndSorting(pageNo, pageSize, sortField, sortDirection);
            List<Client> clients = page.getContent();
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("reverseSortDirection", sortDirection.equals("desc") ? "desc" : "asc");
            model.addAttribute(CLIENTS, clients);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            model.addAttribute("message", exception.getMessage());
        }
        return CLIENTS;
    }

    @PostMapping(value = "/clients")
    public String getClients(Model model, @Param("keyword") String keyword) {
        List<Client> clients = clientService.getClientsWithSearch(keyword);
        model.addAttribute(CLIENTS, clients);
        model.addAttribute("keyword", keyword);
        return CLIENTS;
    }

    @GetMapping(value = "/details/{id}")
    public String getClientById(@PathVariable(name = "id") Long id, Model model) throws NotFoundException {
        try {
            Client client = clientService.getClient(id);
            model.addAttribute(CLIENT, client);
            return "details";
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
    }

    @GetMapping(value = "/add")
    public String createNewClient(Model model) {
        Client newClient = Client.builder().address(Address.builder().build()).build();
        model.addAttribute(CLIENT, newClient);
        model.addAttribute(ACTION, "/add");
        return CLIENT;
    }

    @PostMapping(value = "/add")
    public String saveNewClient(@Valid @ModelAttribute("client") Client client, BindingResult result, Model model) {
        Client existingClient = getExistingClientEmail(client);
        checkIsClientEmailExist(existingClient, result);
        if (result.hasErrors()) {
            model.addAttribute(CLIENT, client);
            model.addAttribute("failure", "failure"); // alert-danger in failure (client.html)
            return CLIENT;
        }
        clientService.save(client);
        return "redirect:/add?success";
    }

    Client getExistingClientEmail(Client client) {
        return clientService.getClient(client.getEmail());
    }

    private static void checkIsClientEmailExist(Client existingClient, BindingResult result) {
       if (existingClient != null && existingClient.getEmail() != null && !existingClient.getEmail().isEmpty()) {
            result.rejectValue("email",
                    null,
                    "PROBLEM");
        }
    }

    @GetMapping(value = "/edit/{id}")
    public String editClient(@PathVariable(name = "id") Long id, Model model) throws NotFoundException {
        try {
        model.addAttribute(CLIENT, clientService.getClient(id));
        model.addAttribute(ACTION, "/edit/" + id);
        return CLIENT;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
    }

    @PostMapping(value = "/edit/{id}")
    public String editClient(@Valid @PathVariable(name = "id") Long id, Client client) throws BadRequestException {
        try {
            clientService.edit(id, client);
            return REDIRECT_CLIENTS_SUCCESS;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new BadRequestException(exception.getMessage());
        }
    }

    @GetMapping(value = "/renew/{id}")
    public String renewClient(@PathVariable(name = "id") Long id, Model model) throws NotFoundException {
        try {
            model.addAttribute(CLIENT, clientService.getClient(id));
            model.addAttribute(ACTION, "/renew/" + id);
            return "renew";
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
    }


    @PostMapping(value = "/renew/{id}")
    public String renewClient(@Valid @PathVariable(name = "id") Long id, Client client) throws BadRequestException {
        try {
            clientService.renew(id, client);
            return REDIRECT_CLIENTS_SUCCESS;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new BadRequestException(exception.getMessage());
        }
    }

    @PostMapping(value = "/delete/{id}")
    public String deleteClient(@PathVariable(name = "id") Long id) throws NotFoundException {
        try {
            clientService.delete(id);
            return REDIRECT_CLIENTS_SUCCESS;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new NotFoundException(exception.getMessage());
        }
    }
}