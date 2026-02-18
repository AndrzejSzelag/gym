package pl.szelag.gym.user.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.szelag.gym.common.exception.DuplicateResourceException;
import pl.szelag.gym.user.controller.UserViewConstant;

/** Global provider for user-related UI context and specialized exception handling for registration flows. */
@ControllerAdvice
@RequiredArgsConstructor
public final class UserControllerAdvice {

    private final MessageSource messageSource;
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    /** @param authentication current security context authenticated principal or null if anonymous */
    @ModelAttribute(UserViewConstant.ATTR_CURRENT_USER)
    public Object addCurrentUser(Authentication authentication) {
        return isAuthenticatedUser(authentication) ? authentication.getPrincipal() : null;
    }

    /** @param ex duplicate resource exception @param model UI model registration view path with error details */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceException(DuplicateResourceException ex, Model model) {
        String localizedMessage = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        model.addAttribute(UserViewConstant.ATTR_EMAIL_ERROR, localizedMessage);
        return UserViewConstant.VIEW_REGISTRATION;
    }

    /** @param authentication context to verify true if user is fully authenticated (not anonymous) */
    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !trustResolver.isAnonymous(authentication);
    }
}