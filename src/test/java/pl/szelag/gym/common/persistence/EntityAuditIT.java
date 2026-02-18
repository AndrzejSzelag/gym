package pl.szelag.gym.common.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import pl.szelag.gym.BaseIT;
import pl.szelag.gym.client.entity.Client;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Integration test for verifying JPA Auditing behavior including timestamps and auditor identity. */
class EntityAuditIT extends BaseIT {

    @Autowired
    private EntityManager entityManager;

    /** Verifies that both timestamps and current auditor are correctly persisted. */
    @Test
    @WithMockUser(username = "andrzej.szelag@gym.pl")
    void shouldSetAuditTimestampsAndAuditorOnPersist() {
        // GIVEN
        Client client = new Client("Andrzej", "Szelag", "audit.it@gym.pl", "123456789", LocalDate.now());

        // WHEN
        entityManager.persist(client);
        entityManager.flush();

        // THEN
        assertThat(client.getCreatedAt()).as("Creation timestamp should be set").isNotNull();
        assertThat(client.getUpdatedAt()).as("Update timestamp should be set").isNotNull();
        assertThat(client.getCreatedBy()).as("Creator should match mock user").isEqualTo("andrzej.szelag@gym.pl");
        assertThat(client.getLastModifiedBy()).as("Modifier should match mock user").isEqualTo("andrzej.szelag@gym.pl");
    }
}