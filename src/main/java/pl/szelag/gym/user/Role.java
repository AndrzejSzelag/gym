package pl.szelag.gym.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity(name = "Role")
@Table(name = "ROLES")
@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public final class Role {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ROLE_GENERATOR")
    @SequenceGenerator(
            name = "ROLE_GENERATOR",
            sequenceName = "ROLE_SEQ",
            allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 30, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}