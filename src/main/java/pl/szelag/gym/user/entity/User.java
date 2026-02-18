package pl.szelag.gym.user.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.szelag.gym.common.persistence.AbstractPerson;
import pl.szelag.gym.utility.StringUtils;

/** System user entity inheriting identity from {@link AbstractPerson} with role-based access control. */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class User extends AbstractPerson {

    /** database primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** pre-encoded security password hash */
    @Column(nullable = false, length = 64)
    private String password;

    /** assigned security role for authorization */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_USERS_ROLE"))
    private Role role;

    /** Initializes a new user with sanitized profile data, password hash, and assigned role. */
    public User(String firstName, String lastName, String email, String password, Role role) {
        updateUserProfile(firstName, lastName, email);
        this.password = password;
        this.role = role;
    }

    /** Updates personal information with automatic sanitization and email normalization. */
    public void updateUserProfile(String firstName, String lastName, String email) {
        setPersonalInfo(
                StringUtils.sanitize(firstName),
                StringUtils.sanitize(lastName),
                StringUtils.normalize(email));
    }

    /** combined first and last name with trimmed whitespace */
    public String getFullName() {
        String first = (getFirstName() != null) ? getFirstName() : "";
        String last = (getLastName() != null) ? getLastName() : "";
        return (first + " " + last).trim();
    }
}