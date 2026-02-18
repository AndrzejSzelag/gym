package pl.szelag.gym.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.factory.UserFactory;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for administrator authentication flow.
 * Verifies login success, failure scenarios, and secure logout.
 */
@AutoConfigureMockMvc
class UserLoginViewIT extends BaseIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "andrzej.szelag@gym.pl";
    private static final String ADMIN_PASS = "admin123";

    @BeforeEach
    void setUp() {
        // GIVEN: Clean database state with predefined administrator data
        userRepository.deleteAllInBatch();

        Role adminRole = roleRepository.findByName(UserRole.ADMINISTRATOR.authority())
                .orElseGet(() -> roleRepository.save(RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority())));

        User admin = UserFactory.fromRegistration(
                new UserDto("Andrzej", "Szelag", ADMIN_EMAIL, passwordEncoder.encode(ADMIN_PASS)),
                adminRole
        );

        userRepository.save(admin);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // WHEN: Submitting valid credentials via HTTPS
        var result = mockMvc.perform(post("/login")
                .secure(true)
                .with(csrf())
                .param("username", ADMIN_EMAIL)
                .param("password", ADMIN_PASS));

        // THEN: User is authenticated and redirected to the dashboard
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"))
                .andExpect(authenticated().withUsername(ADMIN_EMAIL));
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // WHEN: Submitting an incorrect password
        var result = mockMvc.perform(post("/login")
                .secure(true)
                .with(csrf())
                .param("username", ADMIN_EMAIL)
                .param("password", "wrong-password"));

        // THEN: Request is redirected back to login with error flag
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = "ADMINISTRATOR")
    void shouldLogoutSuccessfully() throws Exception {
        // WHEN: Performing a POST logout with valid CSRF token
        var result = mockMvc.perform(post("/logout")
                .secure(true)
                .with(csrf()));

        // THEN: Session is invalidated and user is redirected to login with logout flag
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"))
                .andExpect(unauthenticated());
    }
}