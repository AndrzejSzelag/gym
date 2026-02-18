package pl.szelag.gym.client.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.client.controller.ClientController;
import pl.szelag.gym.client.controller.ClientViewConstant;
import pl.szelag.gym.client.controller.ClientViewHelper;
import pl.szelag.gym.client.dto.AddressDto;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.service.ClientQueryService;
import pl.szelag.gym.common.exception.DuplicateResourceException;

/** * Specialized controller advice for Client management, handling domain conflicts with UI state reconstruction.
 */
@Slf4j
@ControllerAdvice(assignableTypes = ClientController.class)
@RequiredArgsConstructor
public class ClientControllerAdvice {

    /** Service for fetching client data and summaries. */
    private final ClientQueryService clientQueryService;
    /** Helper for view-related logic and state reconstruction. */
    private final ClientViewHelper clientViewHelper;

    /**
     * Handles cases where a duplicate email or resource is detected.
     * * @param ex duplicate exception containing conflict details
     * @param request raw request for parameter extraction
     * @param ra redirect attributes for flash data
     * @param model UI model for re-rendering
     * @return view name or redirect path
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResource(DuplicateResourceException ex,
                                          HttpServletRequest request,
                                          RedirectAttributes ra,
                                          Model model) {
        String uri = request.getRequestURI();
        log.warn("Conflict at {}: {}", uri, ex.getMessage());

        PaginationParams params = new PaginationParams();
        params.setPageNo(safeParseInt(request.getParameter("pageNo"), 1));

        if ("POST".equalsIgnoreCase(request.getMethod()) && uri.matches(".*/clients/\\d+$")) {
            return handleUpdateDuplicate(uri, request, params, ra);
        }
        return handleAddDuplicate(request, params, model);
    }

    /** * Reconstructs the add-client form state with error messages and refreshed client list.
     * * @param request current servlet request
     * @param params current pagination state
     * @param model UI model to populate
     * @return clients page view with error details
     */
    private String handleAddDuplicate(HttpServletRequest request, PaginationParams params, Model model) {
        ClientDto dto = mapRequestToDto(request, null);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, ClientViewConstant.NEW_CLIENT);
        clientViewHelper.rejectDuplicateEmail(bindingResult, dto.getEmail());

        Page<ClientDtoSummary> page = clientQueryService.getClientsPage(params);
        model.addAttribute(ClientViewConstant.CLIENT, ClientDto.empty());

        return clientViewHelper.renderClientsPageWithAddErrors(model, dto, bindingResult, page, params);
    }

    /** * Prepares redirect attributes for the edit view after a duplicate email conflict during update.
     * * @param uri current request URI
     * @param request current servlet request
     * @param params current pagination state
     * @param ra redirect attributes to store errors
     * @return redirect path to the edit form
     */
    private String handleUpdateDuplicate(String uri,
                                         HttpServletRequest request,
                                         PaginationParams params,
                                         RedirectAttributes ra) {
        Long id = extractIdFromUri(uri);
        ClientDto dto = mapRequestToDto(request, id);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, ClientViewConstant.CLIENT);
        clientViewHelper.rejectDuplicateEmail(bindingResult, dto.getEmail());

        return clientViewHelper.redirectWithEditErrors(id, dto, bindingResult, ra, params);
    }

    /** * Extracts form data from HttpServletRequest to rebuild DTO when standard binding is bypassed by exception.
     * * @param request raw request containing form data
     * @param id optional client identifier
     * @return reconstructed ClientDto
     */
    private ClientDto mapRequestToDto(HttpServletRequest request, Long id) {
        AddressDto address = AddressDto.builder()
                .street(request.getParameter("address.street"))
                .streetNumber(request.getParameter("address.streetNumber"))
                .homeNumber(request.getParameter("address.homeNumber"))
                .postCode(request.getParameter("address.postCode"))
                .city(request.getParameter("address.city"))
                .build();

        return ClientDto.builder()
                .id(id)
                .firstName(request.getParameter("firstName"))
                .lastName(request.getParameter("lastName"))
                .email(request.getParameter("email"))
                .phone(request.getParameter("phone"))
                .address(address)
                .build();
    }

    /**
     * Safely parses an integer from a string.
     * * @param value string value to parse
     * @param defaultValue fallback value on error
     * @return parsed integer or default
     */
    private int safeParseInt(String value, int defaultValue) {
        try {
            return (value != null && !value.isBlank()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Extracts the numeric ID from the end of a URI.
     * * @param uri request URI
     * @return extracted ID or null if parsing fails
     */
    private Long extractIdFromUri(String uri) {
        try {
            String[] parts = uri.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}