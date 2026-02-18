package pl.szelag.gym.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.user.dto.UserDto;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserViewHelperTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserViewHelper userViewHelper;

    @Test
    void shouldRedirectWithRegistrationErrors() {
        // GIVEN
        UserDto dto = UserDto.builder()
                .firstName("Andrzej")
                .lastName("Szelag")
                .email("andrzej.szelag@gym.pl")
                .build();

        String expectedBindingResultKey = BindingResult.MODEL_KEY_PREFIX + UserViewConstant.ATTR_USER_DTO;

        // WHEN
        String result = userViewHelper.redirectWithRegistrationErrors(dto, bindingResult, redirectAttributes);

        // THEN
        assertThat(result).isEqualTo("redirect:/register");
        verify(redirectAttributes).addFlashAttribute(UserViewConstant.ATTR_USER_DTO, dto);
        verify(redirectAttributes).addFlashAttribute(expectedBindingResultKey, bindingResult);
    }

    @Test
    void shouldAddSuccessMessage() {
        // GIVEN
        String messageKey = "success.userCreated";
        String localizedMessage = "User registered successfully!";

        when(messageSource.getMessage(eq(messageKey), any(), any(Locale.class)))
                .thenReturn(localizedMessage);

        // WHEN
        userViewHelper.addSuccessMessage(redirectAttributes, messageKey);

        // THEN
        verify(redirectAttributes).addFlashAttribute("successMessage", localizedMessage);
    }
}