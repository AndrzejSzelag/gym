package pl.szelag.gym.common.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void shouldHandleDatabaseExceptions() {
        // Given
        when(messageSource.getMessage(eq("error.database.unavailable"), isNull(), any(Locale.class)))
                .thenReturn("Database is unavailable");
        SQLException exception = new SQLException("Connection failed");

        // When
        String viewName = exceptionHandler.handleDatabaseErrors(exception, model);

        // Then
        assertThat(viewName).isEqualTo("error");
        verify(model).addAttribute("errorMessage", "Database is unavailable");
    }

    @Test
    void shouldHandleDatabaseErrorWithLocalizationFailure() {
        // Given
        when(messageSource.getMessage(eq("error.database.unavailable"), isNull(), any(Locale.class)))
                .thenThrow(new NoSuchMessageException("Key not found"));
        SQLException exception = new SQLException("Connection failed");

        // When
        String viewName = exceptionHandler.handleDatabaseErrors(exception, model);

        // Then
        assertThat(viewName).isEqualTo("error");
        verify(model).addAttribute("errorMessage", "Unexpected system error (Localization fallback)");
    }

    @Test
    void shouldRedirectToRegisterForUserResource() {
        // Given
        String messageKey = "error.user.duplicate";
        when(messageSource.getMessage(eq(messageKey), any(), any(Locale.class)))
                .thenReturn("User already exists");
        DuplicateResourceException exception =
                new DuplicateResourceException("USER", messageKey);

        // When
        String viewName = exceptionHandler.handleDuplicateResource(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/register");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "User already exists");
    }

    @Test
    void shouldRedirectToClientsForClientResource() {
        // Given
        String messageKey = "error.client.duplicate";
        when(messageSource.getMessage(eq(messageKey), any(), any(Locale.class)))
                .thenReturn("Client already exists");
        DuplicateResourceException exception =
                new DuplicateResourceException("CLIENT", messageKey);

        // When
        String viewName = exceptionHandler.handleDuplicateResource(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/clients");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Client already exists");
    }

    @Test
    void shouldPassDuplicateResourceArgsToMessageSource() {
        // Given
        String messageKey = "error.user.duplicate";
        Object[] args = {"test@email.com"};
        when(messageSource.getMessage(eq(messageKey), eq(args), any(Locale.class)))
                .thenReturn("User test@email.com already exists");
        DuplicateResourceException exception =
                new DuplicateResourceException("USER", messageKey, args);

        // When
        String viewName = exceptionHandler.handleDuplicateResource(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/register");
        verify(messageSource).getMessage(eq(messageKey), eq(args), any(Locale.class));
        verify(redirectAttributes).addFlashAttribute("errorMessage", "User test@email.com already exists");
    }

    @Test
    void shouldRedirectToClientsForNonUserResourceTypes() {
        // Given
        String messageKey = "error.product.duplicate";
        when(messageSource.getMessage(eq(messageKey), any(), any(Locale.class)))
                .thenReturn("Product already exists");
        DuplicateResourceException exception =
                new DuplicateResourceException("PRODUCT", messageKey);

        // When
        String viewName = exceptionHandler.handleDuplicateResource(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/clients");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Product already exists");
    }

    @Test
    void shouldRedirectIllegalArgumentToRegister() {
        // Given
        when(messageSource.getMessage(eq("error.invalid.argument"), isNull(), any(Locale.class)))
                .thenReturn("Invalid argument");
        IllegalArgumentException exception = new IllegalArgumentException("error.invalid.argument");

        // When
        String viewName = exceptionHandler.handleIllegalExceptions(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/register");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid argument");
    }

    @Test
    void shouldRedirectIllegalStateToClients() {
        // Given
        when(messageSource.getMessage(eq("error.invalid.state"), isNull(), any(Locale.class)))
                .thenReturn("Invalid state");
        IllegalStateException exception = new IllegalStateException("error.invalid.state");

        // When
        String viewName = exceptionHandler.handleIllegalExceptions(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/clients");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid state");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        // Given
        String messageKey = "error.resource.notfound";
        // IMPORTANT: Use any() instead of isNull() because GymException.getArgs() returns new Object[0].
        when(messageSource.getMessage(eq(messageKey), any(), any(Locale.class)))
                .thenReturn("Resource not found");
        ResourceNotFoundException exception =
                new ResourceNotFoundException(messageKey);

        // When
        String viewName = exceptionHandler.handleResourceNotFound(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/clients");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Resource not found");
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        // Given
        // EntityNotFoundException does not inherit from GymException, so args is null.
        when(messageSource.getMessage(eq("Entity not found"), isNull(), any(Locale.class)))
                .thenReturn("Entity not found");
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        // When
        String viewName = exceptionHandler.handleEntityNotFound(exception, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/clients");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Entity not found");
    }

    @Test
    void shouldHandleGenericException() {
        // Given
        when(messageSource.getMessage(eq("error.unexpected"), isNull(), any(Locale.class)))
                .thenReturn("Unexpected error");
        Exception exception = new RuntimeException("Unexpected error");

        // When
        String viewName = exceptionHandler.handleGenericException(exception, model);

        // Then
        assertThat(viewName).isEqualTo("error");
        verify(model).addAttribute("errorMessage", "Unexpected error");
    }

    @Test
    void shouldHandleLocalizationFailureInGenericException() {
        // Given
        when(messageSource.getMessage(eq("error.unexpected"), isNull(), any(Locale.class)))
                .thenThrow(new NoSuchMessageException("Key not found"));
        Exception exception = new RuntimeException("error.test");

        // When
        String viewName = exceptionHandler.handleGenericException(exception, model);

        // Then
        assertThat(viewName).isEqualTo("error");
        verify(model).addAttribute("errorMessage", "Unexpected system error (Localization fallback)");
    }
}