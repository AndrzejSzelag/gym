package pl.szelag.gym.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.szelag.gym.common.persistence.AuditableEntity;

/** Domain entity representing security authorities. */
@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends AuditableEntity {

    /** unique role identifier */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** authority name string */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** @param name role name for factory instantiation */
    Role(String name) {
        this.name = name;
    }
}