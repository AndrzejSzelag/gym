package pl.szelag.gym.client;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Address")
@Table(name = "ADDRESSES")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDRESS_GENERATOR")
    @SequenceGenerator(name = "ADDRESS_GENERATOR", sequenceName = "ADDRESS_SEQ", allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "STREET", length = 32, nullable = true)
    private String street;

    @Column(name = "STREET_NUMBER", length = 3, nullable = true)
    private String streetNumber;

    @Column(name = "HOME_NUMBER", length = 3, nullable = true)
    private String homeNumber;

    @Column(name = "POST_CODE", length = 6, nullable = true)
    private String postCode;

    @Column(name = "CITY", length = 32, nullable = true)
    private String city;
}