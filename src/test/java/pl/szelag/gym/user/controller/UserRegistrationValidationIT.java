package pl.szelag.gym.user.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import pl.szelag.gym.BaseIT;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationValidationIT extends BaseIT {

    private static final String BINDING_RESULT_KEY =
            org.springframework.validation.BindingResult.MODEL_KEY_PREFIX + UserViewConstant.ATTR_USER_DTO;

    // Spróbuj zmodyfikować performRegister tak:
    private MvcResult performRegister(String firstName, String lastName, String email, String password) throws Exception {
        return mockMvc.perform(post("/register/save")
                        .with(csrf())
                        // Dodaj to, żeby udawać zalogowanego admina:
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("andrzej.szelag@gym.pl").roles("ADMINISTRATOR"))
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    private BindingResult extractBindingResult(MvcResult result) {
        // // GIVEN
        Object brObj = result.getFlashMap().get(BINDING_RESULT_KEY);
        if (brObj == null) {
            System.err.println("!!! BŁĄD: Brak BindingResult w FlashMap !!!");
            System.err.println("Klucz, którego szukałem: " + BINDING_RESULT_KEY);
            System.err.println("Klucze znalezione w FlashMap: " + result.getFlashMap().keySet());
            System.err.println("URL przekierowania: " + result.getResponse().getRedirectedUrl());
            throw new IllegalStateException("Brak BindingResult. Sprawdź czy UserController używa stałej UserViewConstant.ATTR_USER_DTO");
        }
        return (BindingResult) brObj;
    }

    @Nested
    class BlankFieldsTests {
        @Test
        void shouldHandleBlankFields() throws Exception {
            // // GIVEN
            MvcResult result = performRegister("", "", "", "");

            // WHEN
            BindingResult br = extractBindingResult(result);

            // THEN
            assertThat(br.getFieldErrors("firstName")).extracting("code").contains("NotBlank");
            assertThat(br.getFieldErrors("lastName")).extracting("code").contains("NotBlank");
            assertThat(br.getFieldErrors("email")).extracting("code").contains("NotBlank");
            assertThat(br.getFieldErrors("password")).extracting("code").contains("NotBlank");
        }
    }

    @Nested
    class FieldFormatTests {
        @Test
        void shouldHandleInvalidEmail() throws Exception {
            // // GIVEN
            // Andrzej Szelag, andrzej.szelag@gym.pl, ROLE_ADMINISTRATOR
            MvcResult result = performRegister("Andrzej", "Szelag", "not-an-email", "SecurePass123");

            // WHEN
            BindingResult br = extractBindingResult(result);

            // THEN
            assertThat(br.getFieldErrors("email")).extracting("code").contains("Email");
        }

        @ParameterizedTest
        @MethodSource("passwordProvider")
        void shouldValidatePasswordSize(String password) throws Exception {
            // // GIVEN
            MvcResult result = performRegister("Andrzej", "Szelag", "andrzej.szelag@gym.pl", password);

            // WHEN
            BindingResult br = extractBindingResult(result);

            // THEN
            assertThat(br.getFieldErrors("password")).extracting("code").contains("Size");
        }

        static Stream<String> passwordProvider() {
            return Stream.of("123", "a".repeat(65));
        }
    }
}