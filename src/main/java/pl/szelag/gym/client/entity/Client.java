package pl.szelag.gym.client.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.szelag.gym.common.api.AddressProvider;
import pl.szelag.gym.common.persistence.AbstractPerson;
import pl.szelag.gym.utility.StringUtils;

import java.time.LocalDate;

/** * Client aggregate root inheriting identity from {@link AbstractPerson} and managing gym memberships.
 */
@Entity
@Table(name = "clients", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Client extends AbstractPerson {

    /** Database primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Client's primary contact phone number. */
    @Column(nullable = false, length = 9)
    private String phone;

    /** Date when the client was first registered in the system. */
    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;

    /** Current membership expiration date or null if inactive. */
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /** Associated physical address record. */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", foreignKey = @ForeignKey(name = "FK_CLIENTS_ADDRESS"))
    private Address address;

    /** * Initializes a new client with sanitized data and optional registration date.
     *
     * @param firstName        client's first name
     * @param lastName         client's last name
     * @param email            client's email address
     * @param phone            client's phone number
     * @param registrationDate date of registration (defaults to now if null)
     */
    public Client(String firstName, String lastName, String email, String phone, LocalDate registrationDate) {
        updateClientProfile(firstName, lastName, email, phone);
        this.registrationDate = registrationDate != null ? registrationDate : LocalDate.now();
    }

    /** * Updates personal and contact data using internal sanitization rules.
     *
     * @param firstName client's new first name
     * @param lastName  client's new last name
     * @param email     client's new email
     * @param phone     client's new phone number
     */
    public void updateClientProfile(String firstName, String lastName, String email, String phone) {
        setPersonalInfo(StringUtils.sanitize(firstName), StringUtils.sanitize(lastName), StringUtils.normalize(email));
        this.phone = StringUtils.sanitize(phone);
    }

    /** * Links or updates client's physical address via the provided data source.
     *
     * @param provider address data source (provider)
     */
    public void setOrUpdateAddress(AddressProvider provider) {
        if (provider == null) return;
        if (this.address == null) {
            this.address = new Address(provider);
        } else {
            this.address.update(provider);
        }
    }

    /** * Processes a client update request based on the provided command object.
     *
     * @param command data object containing update details
     */
    public void updateClient(ClientUpdateCommand command) {
        if (command == null) return;
        updateClientProfile(command.firstName(), command.lastName(), command.email(), command.phone());
        if (command.expirationDate() != null) setMembershipExpiration(command.expirationDate());
        if (command.address() != null) setOrUpdateAddress(command.address());
    }

    /** * Extends membership duration starting from today or the current expiration date.
     *
     * @param days number of days to add to membership
     */
    public void extendMembership(int days) {
        LocalDate base = (expirationDate != null && expirationDate.isAfter(LocalDate.now()))
                ? expirationDate
                : LocalDate.now();
        expirationDate = base.plusDays(days);
    }

    /** * Sets a specific expiration date, validating that it is not in the past.
     *
     * @param expirationDate date when membership expires
     * @throws IllegalArgumentException if the date is in the past
     */
    public void setMembershipExpiration(LocalDate expirationDate) {
        if (expirationDate != null && expirationDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        this.expirationDate = expirationDate;
    }
}