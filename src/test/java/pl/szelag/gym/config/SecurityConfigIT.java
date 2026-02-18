package pl.szelag.gym.config;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.BaseIT;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SecurityConfig.
 * Verifies authorization rules, HTTPS requirements, and CSRF protection using custom mock annotations.
 */
class SecurityConfigIT extends BaseIT {

    @Test
    @WithMockAuthUser(role = "ROLE_ADMINISTRATOR")
    void shouldAllowAccessToUsersListForAdministratorRole() throws Exception {
        // GIVEN: User with ROLE_ADMINISTRATOR role

        // WHEN & THEN
        mockMvc.perform(get("/users").secure(true))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockAuthUser(email = "andrzej.szelag@gym.pl", fullName = "Andrzej Szelag", role = "ROLE_ADMINISTRATOR")
    void shouldIdentifyAndrzejSzelagAsAdminAndAllowAccessToUsersList() throws Exception {
        // GIVEN: Specific principal for Andrzej Szelag created via custom factory

        // WHEN & THEN
        mockMvc.perform(get("/users").secure(true))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockAuthUser(role = "ROLE_ADMINISTRATOR")
    void shouldAllowAdminToAccessClientsList() throws Exception {
        // GIVEN: Authenticated administrator

        // WHEN & THEN: 404 is acceptable as it confirms the security filter allowed the request
        mockMvc.perform(get("/clients").secure(true))
                .andExpect(status().is(anyOf(is(200), is(404))));
    }

    @Test
    void shouldRedirectToLoginWhenAnonymousAccessesPrivateUrl() throws Exception {
        // GIVEN: No authentication provided

        // WHEN & THEN
        mockMvc.perform(get("/clients").secure(true))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldReturn401ForAsyncRequestWhenUnauthorized() throws Exception {
        // GIVEN: AJAX request from an anonymous user

        // WHEN & THEN: Verifies CustomAuthEntryPoint behavior
        mockMvc.perform(get("/clients")
                        .secure(true)
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockAuthUser(role = "ROLE_ADMIN_TEST")
    void shouldRejectPostWithoutCsrfToken() throws Exception {
        // GIVEN: Authenticated session but missing CSRF token on a state-changing request

        // WHEN & THEN
        mockMvc.perform(post("/register/save")
                        .secure(true)
                        .param("email", "test@gym.pl"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAcceptPostWithCsrfTokenOnPublicEndpoint() throws Exception {
        // GIVEN: Valid CSRF token for a publicly accessible registration endpoint

        // WHEN & THEN
        mockMvc.perform(post("/register/save")
                        .secure(true)
                        .with(csrf())
                        .param("firstName", "Andrzej")
                        .param("lastName", "Szelag")
                        .param("email", "andrzej.szelag@gym.pl")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection());
    }
}