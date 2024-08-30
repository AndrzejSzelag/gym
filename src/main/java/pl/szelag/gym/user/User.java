package pl.szelag.gym.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity(name = "User")
@Table(name = "USERS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"EMAIL"})
})
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "USER_GENERATOR")
    @SequenceGenerator(
            name = "USER_GENERATOR",
            sequenceName = "USER_SEQ",
            allocationSize = 1)
    @Column(name = "ID", updatable = false)
    private Long id;

    @Column(name = "NAME", length = 32, nullable = false)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    private String name;

    @Column(name = "EMAIL", length = 32, nullable = false, unique = true)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Email
    private String email;

    @Column(name = "PASSWORD", length = 64, nullable = false)
    @Size(min = 1, max = 64, message = "{constraint.string.length.notinrange}") // 60 signs for bcrypt
    @ToString.Exclude
    private String password;

    // .ALL -> org.hibernate.PersistentObjectException: detached entity passed to persist: pl.szelag.gym.model.Role
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "USERS_ROLES",
            joinColumns = {
                    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}
    )
    private Collection<Role> roles = new ArrayList<>();
}
