package pl.szelag.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Secure GYM Management System.
 *
 * <p>This class enables Spring Boot auto-configuration, component scanning,
 * and activates JPA Auditing to automatically track entity creation and modification dates.</p>
 */
@SpringBootApplication
@EnableJpaAuditing
public class GymApp {

    /** Bootstraps the application with provided command line arguments. */
    public static void main(String[] args) {
        // Launch the Spring application context
        SpringApplication.run(GymApp.class, args);
    }
}