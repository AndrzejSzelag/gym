package pl.szelag.gym.client.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.PaginationParams;

import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientViewHelperTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private Page<Object> page;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ClientViewHelper clientViewHelper;

    @BeforeEach
    void setUp() {
        // Required for ServletUriComponentsBuilder to work in tests
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/gym");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldAddClientPageAttributes() {
        // GIVEN
        PaginationParams params = new PaginationParams();
        params.setPageNo(1);
        params.setSortField("lastName");

        when(page.getContent()).thenReturn(Collections.emptyList());
        when(page.getTotalPages()).thenReturn(5);
        when(page.getTotalElements()).thenReturn(50L);

        // WHEN
        clientViewHelper.addClientPageAttributes(model, page, params);

        // THEN
        verify(model).addAttribute("clients", Collections.emptyList());
        verify(model).addAttribute("sortField", "lastName");
        verify(model).addAttribute("totalPages", 5);
        verify(model).addAttribute("totalItems", 50L);
        verify(model).addAttribute(eq(ClientViewConstant.CLIENT), any(ClientDto.class));
    }

    @Test
    void shouldPrepareEditModel() {
        // GIVEN
        ClientDto clientDto = new ClientDto();
        Long id = 100L;

        // WHEN
        clientViewHelper.prepareEditModel(model, clientDto, id);

        // THEN
        verify(model).addAttribute(ClientViewConstant.CLIENT, clientDto);
        verify(model).addAttribute("editClientId", id);
        verify(model).addAttribute("showEditModal", true);
    }

    @Test
    void shouldRedirectPreservingQuery() {
        // GIVEN
        PaginationParams params = new PaginationParams();
        params.setPageNo(2);
        params.setKeyword("search");

        // WHEN
        String redirect = clientViewHelper.redirectPreservingQuery(params);

        // THEN
        // Verifying if the redirect URL contains all necessary security and pagination parameters
        assertThat(redirect).contains("/clients", "pageNo=2", "keyword=search");
    }

    @Test
    void shouldRenderClientsPageWithAddErrors() {
        // GIVEN
        ClientDto dto = new ClientDto();
        PaginationParams params = new PaginationParams();
        when(page.getContent()).thenReturn(Collections.emptyList());

        // WHEN
        String view = clientViewHelper.renderClientsPageWithAddErrors(model, dto, bindingResult, page, params);

        // THEN
        assertThat(view).isEqualTo(ClientViewConstant.CLIENTS);
        verify(model).addAttribute("showAddModal", true);
        verify(model).addAttribute(BindingResult.MODEL_KEY_PREFIX + "newClient", bindingResult);
    }

    @Test
    void shouldAddSuccessMessage() {
        // GIVEN
        String key = "success.save";
        when(messageSource.getMessage(eq(key), any(), any(Locale.class))).thenReturn("Saved successfully");

        // WHEN
        clientViewHelper.addSuccessMessage(redirectAttributes, key);

        // THEN
        verify(redirectAttributes).addFlashAttribute("successMessage", "Saved successfully");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        // GIVEN
        String email = "test@gym.pl";

        // WHEN
        clientViewHelper.rejectDuplicateEmail(bindingResult, email);

        // THEN
        verify(bindingResult).rejectValue(eq("email"), eq("error.duplicateClientEmail"), any(), anyString());
    }

    @Test
    void shouldAddClientDetailsToModel() {
        // GIVEN
        ClientDto dto = new ClientDto();

        // WHEN
        clientViewHelper.addClientDetails(model, dto);

        // THEN
        verify(model).addAttribute(ClientViewConstant.CLIENT, dto);
    }

    @Test
    void shouldRedirectWithEditErrors() {
        // GIVEN
        ClientDto dto = new ClientDto();
        Long id = 1L;
        PaginationParams params = new PaginationParams();

        // WHEN
        String redirect = clientViewHelper.redirectWithEditErrors(id, dto, bindingResult, redirectAttributes, params);

        // THEN
        verify(redirectAttributes).addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + ClientViewConstant.CLIENT, bindingResult);
        verify(redirectAttributes).addFlashAttribute(ClientViewConstant.CLIENT, dto);
        verify(redirectAttributes).addFlashAttribute("editClientId", id);
        verify(redirectAttributes).addFlashAttribute("showEditModal", true);
        assertThat(redirect).contains("/clients");
    }
}