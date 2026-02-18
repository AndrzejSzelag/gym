package pl.szelag.gym.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.szelag.gym.user.service.RoleInitializationService;

import javax.sql.DataSource;
import java.sql.Connection;

/** Orchestrates application startup: database health checks and data seeding. */
@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class ApplicationBootstrap implements CommandLineRunner {

    private final RoleInitializationService roleInitializationService;
    private final DataSource dataSource;

    /** @param args startup arguments @throws Exception if bootstrap fails, causing system exit */
    @Override
    public void run(String... args) {
        log.info("--- Startup sequence initiated ---");
        try {
            checkDatabaseHealth();
            roleInitializationService.initializeRoles();
            log.info("--- Bootstrap completed successfully ---");
        } catch (Exception e) {
            log.error("--- Bootstrap FAILED: System shutdown ---", e);
            System.exit(1);
        }
    }

    /** Verifies database connectivity and logs provider metadata. @throws IllegalStateException if connection fails */
    private void checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            String dbInfo = connection.getMetaData().getDatabaseProductName() + " " +
                    connection.getMetaData().getDatabaseProductVersion();
            log.info("Database Health Check: [SUCCESS] | Provider: {}", dbInfo);
        } catch (Exception e) {
            log.error("Database connectivity failure", e);
            throw new IllegalStateException(e);
        }
    }
}