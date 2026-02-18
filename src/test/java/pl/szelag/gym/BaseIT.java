package pl.szelag.gym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for Integration Tests optimized for "Rapid Development" (Rapid Local Testing).
 *
 * Strategy:
 * Uses a persistent local PostgreSQL container (port 5433) instead of Testcontainers
 * to eliminate container startup overhead, significantly speeding up the TDD cycle.
 *
 * Prerequisites:
 * - Docker container 'gym-postgres' must be running (docker-compose up -d).
 * - Connection details and init scripts are managed via 'test-local' profile.
 */
@ActiveProfiles("test-local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = true)
@Transactional
public abstract class BaseIT {

    @Autowired
    protected MockMvc mockMvc;
}