package pl.szelag.gym.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity(name = "Client")
@Table(name = "CLIENTS", uniqueConstraints = {@UniqueConstraint(columnNames = {"EMAIL"})})
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_GENERATOR")
    @SequenceGenerator(name = "CLIENT_GENERATOR", sequenceName = "CLIENT_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "FIRST_NAME", length = 32, nullable = false)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Pattern(regexp = "^[\\p{L}\\'-]*$", message = "{constraint.string.incorrectchar}")
    private String firstName;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "LAST_NAME", length = 32, nullable = false)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Pattern(regexp = "^[\\p{L}\\'-]*$", message = "{constraint.string.incorrectchar}")
    private String lastName;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "EMAIL", length = 32, nullable = false, unique = true)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Email
    private String email;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "PHONE", length = 9, nullable = false)
    @Size(min = 4, max = 9, message = "{constraint.string.length.notinrange}")
    @Pattern(regexp = "[0-9]{3,9}", message = "{constraint.string.incorrectchar}")
    private String phone;

    @Temporal(TemporalType.DATE)
    @Column(name = "registration_date", nullable = false)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate registrationDate = LocalDate.now();

    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date") // nullable = true (default)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate expirationDate = LocalDate.now();

    // Najbardziej wydajne mapowanie dla @OneToOne, aby uniknąć problemów z zapytaniami N+1
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "ID")
    private Address address;
}
