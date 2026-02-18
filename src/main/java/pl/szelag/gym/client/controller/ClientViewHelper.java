package pl.szelag.gym.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.PaginationParams;

import java.util.Locale;

import static pl.szelag.gym.client.controller.ClientViewConstant.CLIENT;
import static pl.szelag.gym.client.controller.ClientViewConstant.NEW_CLIENT;

/** Helper component for managing Client UI state, model attributes, and complex redirect logic. */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientViewHelper {

    private final MessageSource messageSource;

    /** @param model UI model @param page data page @param params pagination metadata */
    public void addClientPageAttributes(Model model,
                                        Page<?> page,
                                        PaginationParams params) {
        model.addAttribute("clients", page.getContent());

        String currentField = (params.getSortField() != null && !params.getSortField().isEmpty())
                ? params.getSortField() : "expirationDate";
        String currentDir = (params.getSortDirection() != null && !params.getSortDirection().isEmpty())
                ? params.getSortDirection() : "desc";

        model.addAttribute("sortField", currentField);
        model.addAttribute("sortDirection", currentDir);
        model.addAttribute("pageSize", params.getPageSize());
        model.addAttribute("currentPage", params.getPageNo());
        model.addAttribute("totalPages", Math.max(page.getTotalPages(), 1));
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("keyword", params.getKeyword());
        model.addAttribute("params", params);

        if (!model.containsAttribute(CLIENT)) {
            model.addAttribute(CLIENT, ClientDto.empty());
        }
    }

    /** @param model UI model @param clientDto data for edit @param id client identifier */
    public void prepareEditModel(Model model,
                                 ClientDto clientDto,
                                 Long id) {
        model.addAttribute(CLIENT, clientDto);
        model.addAttribute("editClientId", id);
        model.addAttribute("showEditModal", true);
    }

    /** @param model UI model @param clientDto details data */
    public void addClientDetails(Model model,
                                 ClientDto clientDto) {
        model.addAttribute(CLIENT, clientDto);
    }

    /** @param params current filter state redirect URI string preserving pagination context */
    public String redirectPreservingQuery(PaginationParams params) {
        PaginationParams p = (params != null) ? params : new PaginationParams();

        return "redirect:" + ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/clients")
                .queryParam("pageNo", p.getPageNo())
                .queryParam("pageSize", p.getPageSize())
                .queryParam("sortField", (p.getSortField() != null && !p.getSortField().isEmpty())
                        ? p.getSortField() : "expirationDate")
                .queryParam("sortDirection", (p.getSortDirection() != null && !p.getSortDirection().isEmpty())
                        ? p.getSortDirection() : "desc")
                .queryParam("keyword", p.getKeyword())
                .build()
                .toUriString();
    }

    /**
     * @param id client ID
     * @param clientDto form data
     * @param bindingResult validation result
     * @param ra redirect attrs @param params context */
    public String redirectWithEditErrors(Long id,
                                         ClientDto clientDto,
                                         BindingResult bindingResult,
                                         RedirectAttributes ra,
                                         PaginationParams params) {
        ra.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + CLIENT, bindingResult);
        ra.addFlashAttribute(CLIENT, clientDto);
        ra.addFlashAttribute("editClientId", id);
        ra.addFlashAttribute("showEditModal", true);
        return redirectPreservingQuery(params);
    }

    /**
     * @param model UI model
     * @param clientDto form data
     * @param bindingResult validation result
     * @param page current data page
     * @param params context */
    public String renderClientsPageWithAddErrors(Model model,
                                                 ClientDto clientDto,
                                                 BindingResult bindingResult,
                                                 Page<?> page,
                                                 PaginationParams params) {
        addClientPageAttributes(model, page, params);
        model.addAttribute(NEW_CLIENT, clientDto);
        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + NEW_CLIENT, bindingResult);
        model.addAttribute("showAddModal", true);
        return ClientViewConstant.CLIENTS;
    }

    /** @param ra redirect attributes @param messageKey i18n key for success notification */
    public void addSuccessMessage(RedirectAttributes ra, String messageKey) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(messageKey, null, locale);
        ra.addFlashAttribute("successMessage", message);
    }

    /** @param bindingResult result to update @param email problematic value */
    public void rejectDuplicateEmail(BindingResult bindingResult, String email) {
        bindingResult.rejectValue("email",
                "error.duplicateClientEmail",
                new Object[]{email},
                "Email already exists!");
    }
}