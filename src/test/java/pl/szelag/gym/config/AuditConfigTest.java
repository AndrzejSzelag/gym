package pl.szelag.gym.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** Unit test for AuditConfig verifying auditor extraction from SecurityContext. */
class AuditConfigTest {

    private final AuditConfig auditConfig = new AuditConfig();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /** Verifies that the auditor is correctly returned when user is authenticated. */
    @Test
    void shouldReturnAuditorWhenAuthenticated() {
        // GIVEN
        String expectedEmail = "andrzej.szelag@gym.pl";
        Authentication auth = Mockito.mock(Authentication.class);
        SecurityContext context = Mockito.mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(expectedEmail);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // WHEN
        Optional<String> auditor = auditConfig.auditorProvider().getCurrentAuditor();

        // THEN
        assertThat(auditor).contains(expectedEmail);
    }

    /** Verifies that empty optional is returned when no authentication exists. */
    @Test
    void shouldReturnEmptyWhenNotAuthenticated() {
        // GIVEN
        SecurityContextHolder.clearContext();

        // WHEN
        Optional<String> auditor = auditConfig.auditorProvider().getCurrentAuditor();

        // THEN
        assertThat(auditor).isEmpty();
    }
}