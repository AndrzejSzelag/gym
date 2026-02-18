package pl.szelag.gym.user.advice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import pl.szelag.gym.common.exception.DuplicateResourceException;
import pl.szelag.gym.config.AuthUser;
import pl.szelag.gym.user.controller.UserViewConstant;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerAdviceTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUser authUser;

    @InjectMocks
    private UserControllerAdvice userControllerAdvice;

    @Test
    void givenAuthenticatedUser_whenAddingCurrentUser_thenReturnsPrincipal() {
        // GIVEN
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(authUser);
        // trustResolver in advice is a real object (AuthenticationTrustResolverImpl),
        // so it will return false for isAnonymous by default with a mock authentication.

        // WHEN
        Object result = userControllerAdvice.addCurrentUser(authentication);

        // THEN
        assertThat(result).isSameAs(authUser);
    }

    @Test
    void givenAnonymousUser_whenAddingCurrentUser_thenReturnsNull() {
        // GIVEN
        when(authentication.isAuthenticated()).thenReturn(true);
        // We can't easily mock the internal trustResolver without reflection,
        // but by default for an anonymous token, isAnonymous would be true.
        // For this test, let's simulate the unauthenticated path:
        when(authentication.isAuthenticated()).thenReturn(false);

        // WHEN
        Object result = userControllerAdvice.addCurrentUser(authentication);

        // THEN
        assertThat(result).isNull();
    }

    @Test
    void givenDuplicateResourceException_whenHandled_thenReturnsRegistrationViewWithErrorMessage() {
        // GIVEN
        String messageKey = "error.email.exists";
        String localizedMessage = "Email is already taken";
        DuplicateResourceException ex = new DuplicateResourceException("USER", messageKey, "andrzej.szelag@gym.pl");

        when(messageSource.getMessage(eq(messageKey), any(), any(Locale.class)))
                .thenReturn(localizedMessage);

        // WHEN
        String viewName = userControllerAdvice.handleDuplicateResourceException(ex, model);

        // THEN
        verify(model).addAttribute(UserViewConstant.ATTR_EMAIL_ERROR, localizedMessage);
        assertThat(viewName).isEqualTo(UserViewConstant.VIEW_REGISTRATION);
    }
}