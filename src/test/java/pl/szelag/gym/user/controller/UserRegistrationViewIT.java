package pl.szelag.gym.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.FlashMap;
import pl.szelag.gym.BaseIT;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UI-focused integration tests for User registration.
 * Ensures that validation errors are correctly displayed in the HTML view after a redirect (PRG pattern).
 */
class UserRegistrationViewIT extends BaseIT {

    // ====================
    // Helper Method
    // ====================

    private FlashMap submitRegistration(String firstName, String lastName, String email, String password) throws Exception {
        // GIVEN: Form submission with validation triggers
        MvcResult postResult = mockMvc.perform(post("/register/save")
                        .secure(true)
                        .with(csrf())
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        return postResult.getFlashMap();
    }

    // ====================
    // Tests
    // ====================

    @Test
    void shouldDisplayPasswordLengthError() throws Exception {
        // GIVEN: Short password submission
        FlashMap flashAttributes = submitRegistration("Jan", "Kowalski", "jan.kowalski@gym.pl", "123");

        // WHEN: Returning to the registration form via redirect
        var response = mockMvc.perform(get("/register")
                .secure(true)
                .flashAttrs(flashAttributes));

        // THEN: The HTML view renders the correct validation message
        response.andExpect(status().isOk())
                .andExpect(content().string(containsString("Password must be between 6 and 64 characters")));
    }

    @Test
    void shouldDisplayMultipleValidationErrors() throws Exception {
        // GIVEN: Submission with multiple invalid fields
        FlashMap flashAttributes = submitRegistration("", "K", "invalid-email", "");

        // WHEN: Navigating back to the registration page with Flash attributes
        var response = mockMvc.perform(get("/register")
                .secure(true)
                .flashAttrs(flashAttributes));

        // THEN: All expected error messages are present in the DOM
        response.andExpect(status().isOk())
                .andExpect(content().string(containsString("First name is required")))
                .andExpect(content().string(containsString("Last name must be between 2 and 50 characters")))
                .andExpect(content().string(containsString("Invalid email address")))
                .andExpect(content().string(containsString("Password is required")));
    }
}