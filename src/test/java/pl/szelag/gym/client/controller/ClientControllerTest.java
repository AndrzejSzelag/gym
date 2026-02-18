package pl.szelag.gym.client.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.client.dto.ClientDto;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.service.ClientCommandService;
import pl.szelag.gym.client.service.ClientQueryService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientQueryService clientQueryService;

    @Mock
    private ClientCommandService clientCommandService;

    @Mock
    private ClientViewHelper clientViewHelper;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ClientController clientController;

    private PaginationParams params;
    private ClientDto testDto;

    @BeforeEach
    void setUp() {
        params = new PaginationParams();
        testDto = ClientDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .build();
    }

    @Test
    void showClientsList_Success() {
        // GIVEN
        Page<ClientDtoSummary> page = new PageImpl<>(Collections.emptyList());
        when(clientQueryService.getClientsPage(params)).thenReturn(page);

        // WHEN
        String view = clientController.showClientsList(params, model);

        // THEN
        assertEquals(ClientViewConstant.CLIENTS, view);
        verify(clientViewHelper).addClientPageAttributes(model, page, params);
        verify(model).addAttribute(eq(ClientViewConstant.NEW_CLIENT), any(ClientDto.class));
    }

    @Test
    void showClientsList_RedirectWhenPageOutOfBounds() {
        // GIVEN
        params.setPageNo(10);
        Page<ClientDtoSummary> page = new PageImpl<>(Collections.emptyList()); // total pages = 0
        when(clientQueryService.getClientsPage(params)).thenReturn(page);
        when(clientViewHelper.redirectPreservingQuery(params)).thenReturn("redirect:/clients?page=0");

        // WHEN
        String view = clientController.showClientsList(params, model);

        // THEN
        assertEquals("redirect:/clients?page=0", view);
    }

    @Test
    void createClient_Success() {
        // GIVEN
        when(bindingResult.hasErrors()).thenReturn(false);
        when(clientViewHelper.redirectPreservingQuery(params)).thenReturn("redirect:/clients");

        // WHEN
        String view = clientController.createClient(testDto, bindingResult, params, model, redirectAttributes);

        // THEN
        verify(clientCommandService).createClient(testDto);
        verify(clientViewHelper).addSuccessMessage(redirectAttributes, "success.clientCreated");
        assertEquals("redirect:/clients", view);
    }

    @Test
    void createClient_ValidationError() {
        // GIVEN
        when(bindingResult.hasErrors()).thenReturn(true);
        Page<ClientDtoSummary> page = new PageImpl<>(Collections.emptyList());
        when(clientQueryService.getClientsPage(params)).thenReturn(page);

        // WHEN
        String view = clientController.createClient(testDto, bindingResult, params, model, redirectAttributes);

        // THEN
        verify(clientViewHelper).renderClientsPageWithAddErrors(model, testDto, bindingResult, page, params);
        assertEquals(ClientViewConstant.CLIENTS, view);
    }

    @Test
    void showEditForm_Success() {
        // GIVEN
        Long id = 1L;
        Page<ClientDtoSummary> page = new PageImpl<>(Collections.emptyList());
        when(clientQueryService.getClientsPage(params)).thenReturn(page);
        when(clientQueryService.getClientDto(id)).thenReturn(testDto);
        testDto.setAddress(null); // Force address initialization check

        // WHEN
        String view = clientController.showEditForm(id, params, model);

        // THEN
        verify(clientViewHelper).prepareEditModel(model, testDto, id);
        assertEquals(ClientViewConstant.CLIENTS, view);
    }

    @Test
    void updateClient_Success() {
        // GIVEN
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(clientViewHelper.redirectPreservingQuery(params)).thenReturn("redirect:/clients");

        // WHEN
        String view = clientController.updateClient(id, testDto, bindingResult, params, redirectAttributes);

        // THEN
        verify(clientCommandService).updateClient(id, testDto);
        verify(clientViewHelper).addSuccessMessage(redirectAttributes, "success.clientEdited");
        assertEquals("redirect:/clients", view);
    }

    @Test
    void deleteClient_Success() {
        // GIVEN
        Long id = 1L;
        when(clientViewHelper.redirectPreservingQuery(params)).thenReturn("redirect:/clients");

        // WHEN
        String view = clientController.deleteClient(id, params, redirectAttributes);

        // THEN
        verify(clientCommandService).deleteClient(id);
        verify(clientViewHelper).addSuccessMessage(redirectAttributes, "success.clientDeleted");
        assertEquals("redirect:/clients", view);
    }

    @Test
    void renewMembership_Success() {
        // GIVEN
        Long id = 1L;
        when(clientViewHelper.redirectPreservingQuery(params)).thenReturn("redirect:/clients");

        // WHEN
        String view = clientController.renewMembership(id, params, redirectAttributes);

        // THEN
        verify(clientCommandService).extendMembership(id);
        verify(clientViewHelper).addSuccessMessage(redirectAttributes, "success.membershipRenewed");
        assertEquals("redirect:/clients", view);
    }

    @Test
    void getClientDetails_ReturnsFragment() {
        // GIVEN
        Long id = 1L;
        when(clientQueryService.getClientDto(id)).thenReturn(testDto);

        // WHEN
        String view = clientController.getClientDetails(id, model);

        // THEN
        verify(clientViewHelper).addClientDetails(model, testDto);
        assertEquals("details-modal :: detailsModalContent", view);
    }

    @Test
    void updateClient_ValidationError() {
        // GIVEN
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);
        when(clientViewHelper.redirectWithEditErrors(id, testDto, bindingResult, redirectAttributes, params))
                .thenReturn("redirect:/clients/edit/1");

        // WHEN
        String view = clientController.updateClient(id, testDto, bindingResult, params, redirectAttributes);

        // THEN
        verify(clientViewHelper).redirectWithEditErrors(id, testDto, bindingResult, redirectAttributes, params);
        assertEquals("redirect:/clients/edit/1", view);
    }

}