package pl.szelag.gym;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GymApp {

    public static void main(String[] args) {
        try {
            SpringApplication.run(GymApp.class, args);
            log.info("Gym application started");
        } catch (Exception exception) {
            log.warn("Error in application, error message: {}", exception.getMessage());
        }
    }
}