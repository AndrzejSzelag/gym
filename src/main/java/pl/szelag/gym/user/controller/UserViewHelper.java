package pl.szelag.gym.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.user.dto.UserDto;

/** UI helper for managing User-related web flows, validation state persistence, and localized notifications. */
@Component
@RequiredArgsConstructor
public class UserViewHelper {

    private final MessageSource messageSource;

    /**
     * @param dto form data to be preserved
     * @param result validation errors
     * @param attributes redirect attributes for Flash scope
     * redirect path to the registration form
     */
    public String redirectWithRegistrationErrors(UserDto dto, BindingResult result, RedirectAttributes attributes) {
        String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + UserViewConstant.ATTR_USER_DTO;

        attributes.addFlashAttribute(bindingResultKey, result);
        attributes.addFlashAttribute(UserViewConstant.ATTR_USER_DTO, dto);

        return "redirect:/register";
    }

    /**
     * @param attributes redirect attributes for Flash scope
     * @param messageKey i18n key for the success notification
     */
    public void addSuccessMessage(RedirectAttributes attributes, String messageKey) {
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        attributes.addFlashAttribute("successMessage", message);
    }
}