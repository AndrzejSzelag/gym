package pl.szelag.gym.client.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.szelag.gym.common.api.AddressProvider;
import pl.szelag.gym.common.persistence.AuditableEntity;
import pl.szelag.gym.utility.StringUtils;

/** Address entity managed as a mutable part of the Client aggregate, implementing {@link AddressProvider}. */
@Entity
@Table(name = "addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Address extends AuditableEntity implements AddressProvider {

    /** database primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** sanitized street name */
    @Column(length = 50)
    @ToString.Include
    private String street;

    /** sanitized street number */
    @Column(name = "street_number", length = 6)
    @ToString.Include
    private String streetNumber;

    /** sanitized home or apartment number */
    @Column(name = "home_number", length = 6)
    private String homeNumber;

    /** sanitized postal code */
    @Column(name = "post_code", length = 6)
    private String postCode;

    /** sanitized city name */
    @Column(length = 50)
    @ToString.Include
    private String city;

    /** Initializes a new address instance using data from the provided provider. */
    public Address(AddressProvider provider) {
        update(provider);
    }

    /** Updates all address fields with automatic sanitization from the given provider. */
    public void update(AddressProvider provider) {
        if (provider == null) return;

        this.street = StringUtils.sanitize(provider.getStreet());
        this.streetNumber = StringUtils.sanitize(provider.getStreetNumber());
        this.homeNumber = StringUtils.sanitize(provider.getHomeNumber());
        this.postCode = StringUtils.sanitize(provider.getPostCode());
        this.city = StringUtils.sanitize(provider.getCity());
    }
}