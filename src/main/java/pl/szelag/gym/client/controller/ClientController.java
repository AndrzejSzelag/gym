package pl.szelag.gym.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.service.ClientCommandService;
import pl.szelag.gym.client.service.ClientQueryService;

import static pl.szelag.gym.client.controller.ClientViewConstant.CLIENT;
import static pl.szelag.gym.client.controller.ClientViewConstant.NEW_CLIENT;

/** * Controller managing client lifecycles and membership operations.
 */
@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    /** Query service for client data. */
    private final ClientQueryService clientQueryService;
    /** Command service for client operations. */
    private final ClientCommandService clientCommandService;
    /** Helper for view-related operations. */
    private final ClientViewHelper clientViewHelper;

    /**
     * Displays the list of clients with pagination and search.
     *
     * @param params pagination and search criteria
     * @param model UI model to populate
     * @return clients list view or redirect
     */
    @GetMapping
    public String showClientsList(PaginationParams params, Model model) {
        Page<ClientDtoSummary> page = clientQueryService.getClientsPage(params);

        if (params.getPageNo() > page.getTotalPages() && page.getTotalPages() > 0) {
            return clientViewHelper.redirectPreservingQuery(params);
        }

        clientViewHelper.addClientPageAttributes(model, page, params);
        model.addAttribute(NEW_CLIENT, ClientDto.empty());

        return ClientViewConstant.CLIENTS;
    }

    /**
     * Handles creation of a new client.
     *
     * @param clientDto new client data
     * @param bindingResult validation results
     * @param params context preservation for redirection
     * @param model UI model for re-rendering on error
     * @param redirectAttributes attributes for flash messages
     * @return redirect or re-render view on error
     */
    @PostMapping
    public String createClient(@Valid @ModelAttribute(NEW_CLIENT) ClientDto clientDto,
                               BindingResult bindingResult,
                               PaginationParams params,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Page<ClientDtoSummary> page = clientQueryService.getClientsPage(params);
            clientViewHelper.renderClientsPageWithAddErrors(model, clientDto, bindingResult, page, params);
            return ClientViewConstant.CLIENTS;
        }

        clientCommandService.createClient(clientDto);
        clientViewHelper.addSuccessMessage(redirectAttributes, "success.clientCreated");

        return clientViewHelper.redirectPreservingQuery(params);
    }

    /**
     * Shows the edit form for a specific client.
     *
     * @param id client identifier
     * @param params context preservation
     * @param model UI model
     * @return list view with active edit form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, PaginationParams params, Model model) {
        Page<ClientDtoSummary> page = clientQueryService.getClientsPage(params);
        clientViewHelper.addClientPageAttributes(model, page, params);

        ClientDto clientDto = clientQueryService.getClientDto(id);
        if (clientDto.getAddress() == null) {
            clientDto.setAddress(AddressDto.empty());
        }

        clientViewHelper.prepareEditModel(model, clientDto, id);
        model.addAttribute(NEW_CLIENT, ClientDto.empty());

        return ClientViewConstant.CLIENTS;
    }

    /**
     * Processes client data update.
     *
     * @param id client identifier
     * @param clientDto updated data
     * @param bindingResult validation results
     * @param params context preservation
     * @param redirectAttributes flash attributes
     * @return redirect path
     */
    @PostMapping("/{id}")
    public String updateClient(@PathVariable("id") Long id,
                               @Valid @ModelAttribute(CLIENT) ClientDto clientDto,
                               BindingResult bindingResult,
                               PaginationParams params,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return clientViewHelper.redirectWithEditErrors(id, clientDto, bindingResult, redirectAttributes, params);
        }

        clientCommandService.updateClient(id, clientDto);
        clientViewHelper.addSuccessMessage(redirectAttributes, "success.clientEdited");

        return clientViewHelper.redirectPreservingQuery(params);
    }

    /**
     * Renews membership for a client.
     *
     * @param id client identifier for membership extension
     * @param params context preservation
     * @param redirectAttributes flash attributes
     * @return redirect preserving current page/filters
     */
    @PostMapping("/{id}/renew")
    public String renewMembership(@PathVariable("id") Long id,
                                  PaginationParams params,
                                  RedirectAttributes redirectAttributes) {
        clientCommandService.extendMembership(id);
        clientViewHelper.addSuccessMessage(redirectAttributes, "success.membershipRenewed");
        return clientViewHelper.redirectPreservingQuery(params);
    }

    /**
     * Deletes a client from the system.
     *
     * @param id client identifier for removal
     * @param params context preservation
     * @param redirectAttributes flash attributes
     * @return redirect with success message
     */
    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable("id") Long id,
                               PaginationParams params,
                               RedirectAttributes redirectAttributes) {
        clientCommandService.deleteClient(id);
        clientViewHelper.addSuccessMessage(redirectAttributes, "success.clientDeleted");
        return clientViewHelper.redirectPreservingQuery(params);
    }

    /**
     * Returns a fragment with client details.
     *
     * @param id client identifier
     * @param model UI model
     * @return HTML fragment for modal display
     */
    @GetMapping("/{id}/details")
    public String getClientDetails(@PathVariable("id") Long id, Model model) {
        ClientDto clientDto = clientQueryService.getClientDto(id);
        clientViewHelper.addClientDetails(model, clientDto);
        return "details-modal :: detailsModalContent";
    }
}