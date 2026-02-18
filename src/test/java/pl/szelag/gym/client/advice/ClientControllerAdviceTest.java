package pl.szelag.gym.client.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.client.controller.ClientViewHelper;
import pl.szelag.gym.client.service.ClientQueryService;
import pl.szelag.gym.common.exception.DuplicateResourceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerAdviceTest {

    @Mock
    private ClientQueryService clientQueryService;

    @Mock
    private ClientViewHelper clientViewHelper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ClientControllerAdvice advice;

    @BeforeEach
    void setUp() {
        lenient().when(request.getParameter("email")).thenReturn("andrzej.szelag@gym.pl");
        lenient().when(request.getParameter("firstName")).thenReturn("Andrzej");
        lenient().when(request.getParameter("lastName")).thenReturn("Szelag");
    }

    @Test
    void shouldHandleDuplicateOnAdd() {
        // GIVEN
        DuplicateResourceException ex = new DuplicateResourceException("msg", "code", new Object[]{});
        when(request.getRequestURI()).thenReturn("/clients");
        when(request.getMethod()).thenReturn("POST");
        when(clientQueryService.getClientsPage(any())).thenReturn(Page.empty());
        when(clientViewHelper.renderClientsPageWithAddErrors(any(), any(), any(), any(), any()))
                .thenReturn("client/index");

        // WHEN
        String result = advice.handleDuplicateResource(ex, request, redirectAttributes, model);

        // THEN
        assertEquals("client/index", result);
        verify(clientViewHelper).rejectDuplicateEmail(any(), eq("andrzej.szelag@gym.pl"));
    }

    @Test
    void shouldHandleDuplicateOnUpdate() {
        // GIVEN
        DuplicateResourceException ex = new DuplicateResourceException("msg", "code", new Object[]{});
        when(request.getRequestURI()).thenReturn("/clients/123");
        when(request.getMethod()).thenReturn("POST");
        when(clientViewHelper.redirectWithEditErrors(eq(123L), any(), any(), any(), any()))
                .thenReturn("redirect:/edit");

        // WHEN
        String result = advice.handleDuplicateResource(ex, request, redirectAttributes, model);

        // THEN
        assertEquals("redirect:/edit", result);
        verify(clientViewHelper).rejectDuplicateEmail(any(), eq("andrzej.szelag@gym.pl"));
    }

    @Test
    void shouldFallbackToDefaultPageOnInvalidParam() {
        // GIVEN
        DuplicateResourceException ex = new DuplicateResourceException("msg", "code", new Object[]{});
        when(request.getRequestURI()).thenReturn("/clients");
        when(request.getParameter("pageNo")).thenReturn("invalid");
        when(clientQueryService.getClientsPage(any())).thenReturn(Page.empty());

        // WHEN
        advice.handleDuplicateResource(ex, request, redirectAttributes, model);

        // THEN
        verify(clientQueryService).getClientsPage(argThat(p -> p.getPageNo() == 1));
    }
}