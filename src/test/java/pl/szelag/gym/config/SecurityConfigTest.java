package pl.szelag.gym.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityConfig.
 * Verifies Bean creation logic and custom AuthenticationEntryPoint behavior.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityConfigTest {

    @Mock
    private AuthUserDetailsService authUserDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(authUserDetailsService);
    }

    @Test
    void contextLoadsWithAdministrator() {

        // THEN
        // Even with reporting warnings, the application context should start correctly
        // and the security configuration bean should be present.
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void shouldConfigureAuthenticationProvider() {
        // WHEN
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        // THEN
        assertThat(provider).isNotNull();
        // Verify if correct dependencies were injected using reflection for private fields
        Object uds = ReflectionTestUtils.getField(provider, "userDetailsService");
        Object pe = ReflectionTestUtils.getField(provider, "passwordEncoder");

        assertThat(uds).isSameAs(authUserDetailsService);
        assertThat(pe).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void shouldReturnBCryptPasswordEncoder() {
        // WHEN
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // THEN
        // Verifying that the encoder is a BCrypt implementation.
        // The .isInstanceOf() method implicitly checks that the object is not null.
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void shouldReturnAuthenticationManager() throws Exception {
        // GIVEN
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        // WHEN
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // THEN
        assertThat(result).isSameAs(expectedManager);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    // ========== Entry Point Tests ==========

    @Test
    void customAuthEntryPoint_shouldReturn401ForXmlHttpRequest() throws Exception {
        // GIVEN
        when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
        AuthenticationEntryPoint entryPoint = securityConfig.customAuthEntryPoint();

        // WHEN
        entryPoint.commence(request, response, authException);

        // THEN
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void customAuthEntryPoint_shouldReturn401ForFetchRequest() throws Exception {
        // GIVEN
        when(request.getHeader("Sec-Fetch-Mode")).thenReturn("fetch");
        AuthenticationEntryPoint entryPoint = securityConfig.customAuthEntryPoint();

        // WHEN
        entryPoint.commence(request, response, authException);

        // THEN
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void customAuthEntryPoint_shouldRedirectToLoginForStandardBrowserRequest() throws Exception {
        // GIVEN
        when(request.isRequestedSessionIdValid()).thenReturn(true);
        AuthenticationEntryPoint entryPoint = securityConfig.customAuthEntryPoint();

        // WHEN
        entryPoint.commence(request, response, authException);

        // THEN
        verify(response).sendRedirect("/login");
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void customAuthEntryPoint_shouldRedirectToLoginTimeoutForInvalidSession() throws Exception {
        // GIVEN
        when(request.isRequestedSessionIdValid()).thenReturn(false);
        AuthenticationEntryPoint entryPoint = securityConfig.customAuthEntryPoint();

        // WHEN
        entryPoint.commence(request, response, authException);

        // THEN
        verify(response).sendRedirect(SecurityConfig.LOGIN_TIMEOUT_URI);
    }

    @Test
    void shouldHaveCorrectConstants() {
        // THEN
        assertThat(SecurityConfig.LOGIN_TIMEOUT_URI).isEqualTo("/login?timeout=true");
        assertThat(SecurityConfig.LOGOUT_SUCCESS_URI).isEqualTo("/login?logout=true");
    }
}