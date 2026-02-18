package pl.szelag.gym.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

/** Abstract base class providing common personal identity fields and audit tracking. */
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public abstract class AbstractPerson extends AuditableEntity {

    /** person's first name */
    @Column(name = "first_name", nullable = false, length = 50)
    @ToString.Include
    private String firstName;

    /** person's last name */
    @Column(name = "last_name", nullable = false, length = 50)
    @ToString.Include
    private String lastName;

    /** unique normalized email address */
    @Column(nullable = false, unique = true, length = 64)
    @ToString.Include
    private String email;

    /** Internal helper to update personal identity fields at once. */
    protected void setPersonalInfo(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}