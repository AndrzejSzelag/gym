package pl.szelag.gym.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.config.WithMockAuthUser;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.Role;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.factory.UserFactory;
import pl.szelag.gym.user.identity.UserRole;
import pl.szelag.gym.user.repository.RoleRepository;
import pl.szelag.gym.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class UserControllerIT extends BaseIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void cleanUp() {
        // GIVEN: Clean state with required roles
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
        roleRepository.save(RoleFactory.systemRole(UserRole.ADMINISTRATOR.authority()));
    }

    @Test
    void givenLoginPageRequest_whenRequestingLogin_thenReturnsLoginView() throws Exception {
        // GIVEN: Public access to login page

        // WHEN & THEN
        mockMvc.perform(get("/login").secure(true)) // Added .secure(true)
                .andExpect(status().isOk())
                .andExpect(view().name(UserViewConstant.VIEW_LOGIN));
    }

    @Test
    void givenRegistrationPageRequest_whenFirstAccess_thenReturnsRegistrationViewWithEmptyDto() throws Exception {
        // GIVEN: Public access to registration page

        // WHEN & THEN
        mockMvc.perform(get("/register").secure(true))
                .andExpect(status().isOk())
                .andExpect(view().name(UserViewConstant.VIEW_REGISTRATION))
                .andExpect(model().attributeExists(UserViewConstant.ATTR_USER_DTO));
    }

    @Test
    void givenRegistrationData_whenPerformingRegistration_thenUserIsSavedAndRedirectedToLogin() throws Exception {
        // GIVEN
        String email = "andrzej.szelag@gym.pl";

        // WHEN
        mockMvc.perform(post("/register/save")
                        .secure(true)
                        .with(csrf()) // REQUIRED for POST
                        .param("firstName", "Andrzej")
                        .param("lastName", "Szelag")
                        .param("email", email)
                        .param("password", "password123"))
                // THEN
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void givenExistingUserEmail_whenRegisteringWithSameEmail_thenRedirectsWithErrorMessage() throws Exception {
        // GIVEN: Pre-existing user in database
        Role role = roleRepository.findByName(UserRole.ADMINISTRATOR.authority()).orElseThrow();
        userRepository.save(new User("Andrzej", "Szelag", "andrzej.szelag@gym.pl", "pass", role));

        // WHEN: Attempting to register with the same email
        mockMvc.perform(post("/register/save")
                        .secure(true)
                        .with(csrf())
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("email", "andrzej.szelag@gym.pl")
                        .param("password", "password123"))
                // THEN: According to your logs, the system performs a redirect to /register
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "User with this email already exists!"));
    }

    @Test
    @WithMockAuthUser(role = "ROLE_ADMINISTRATOR")
    void givenAuthenticatedAdmin_whenRequestingUsersList_thenListIsDisplayed() throws Exception {
        // GIVEN
        Role role = roleRepository.findByName(UserRole.ADMINISTRATOR.authority()).orElseThrow();
        userRepository.save(UserFactory.fromRegistration(
                new UserDto("Jan", "Kowalski", "jan@gym.pl", "pass"), role));

        // WHEN
        ResultActions response = mockMvc.perform(get("/users").secure(true));

        // THEN
        response.andExpect(status().isOk())
                .andExpect(view().name(UserViewConstant.VIEW_USERS_LIST))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("totalItems", 1));
    }
}