package pl.szelag.gym.common.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

/** Global interceptor for application-wide exception handling and localization. */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /** Model attribute key for error messages. */
    public static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_CLIENTS = "redirect:/clients";
    private static final String REDIRECT_REGISTER = "redirect:/register";

    private final MessageSource messageSource;

    /** @param ex database or transaction exception @param model UI model error view name */
    @ExceptionHandler({
            CannotCreateTransactionException.class,
            JDBCConnectionException.class,
            DataAccessException.class,
            SQLException.class
    })
    public String handleDatabaseErrors(Exception ex, Model model) {
        log.error("Infrastructure failure [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage());
        model.addAttribute(ERROR_MESSAGE, getLocalizedMessage("error.database.unavailable", null));
        return "error";
    }

    /** @param ex domain conflict exception @param attrs flash attributes for redirect redirect path */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResource(DuplicateResourceException ex, RedirectAttributes attrs) {
        String msg = getLocalizedMessage(ex.getMessage(), ex.getArgs());
        log.warn("Domain conflict [{}]: {}", ex.getResourceType(), msg);
        String path = "USER".equals(ex.getResourceType()) ? REDIRECT_REGISTER : REDIRECT_CLIENTS;
        attrs.addFlashAttribute(ERROR_MESSAGE, msg);
        return path;
    }

    /** @param ex state or argument exception @param attrs flash attributes dynamic redirect path */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public String handleIllegalExceptions(RuntimeException ex, RedirectAttributes attrs) {
        String path = (ex instanceof IllegalArgumentException) ? REDIRECT_REGISTER : REDIRECT_CLIENTS;
        return handleRedirectException(ex, attrs, path);
    }

    /** @param ex resource missing exception @param attrs flash attributes redirect to clients */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, RedirectAttributes attrs) {
        return handleRedirectException(ex, attrs, REDIRECT_CLIENTS);
    }

    /** @param ex JPA entity missing exception @param attrs flash attributes redirect to clients */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException ex, RedirectAttributes attrs) {
        return handleRedirectException(ex, attrs, REDIRECT_CLIENTS);
    }

    /** @param ex unhandled exception @param model UI model generic error view */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unhandled exception:", ex);
        model.addAttribute(ERROR_MESSAGE, getLocalizedMessage("error.unexpected", null));
        return "error";
    }

    /** @param ex source exception @param attrs attributes to populate @param path target redirect path */
    private String handleRedirectException(Exception ex, RedirectAttributes attrs, String path) {
        Object[] args = (ex instanceof GymException ge) ? ge.getArgs() : null;
        attrs.addFlashAttribute(ERROR_MESSAGE, getLocalizedMessage(ex.getMessage(), args));
        return path;
    }

    /** @param key i18n message key @param args message arguments localized string or fallback */
    private String getLocalizedMessage(String key, Object[] args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("I18n key resolution failed: {}", key);
            return "Unexpected system error (Localization fallback)";
        }
    }
}