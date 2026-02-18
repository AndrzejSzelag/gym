package pl.szelag.gym.client.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.szelag.gym.common.api.AddressProvider;

/**
 * Data Transfer Object for address input.
 * Supports partial form submission by allowing empty strings and implements
 * {@link AddressProvider} for direct consumption by domain factories.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddressDto implements AddressProvider {

    /** Street name (maximum 50 characters). */
    @Size(max = 50, message = "{constraint.string.length.max}")
    @Pattern(regexp = "^$|^[\\p{L}0-9\\s\\.-]+$", message = "{validation.street.pattern}")
    private String street;

    /** Building or house number (maximum 6 characters). */
    @Size(max = 6, message = "{constraint.string.length.max}")
    @Pattern(regexp = "^$|^[0-9A-Za-z\\s/-]+$", message = "{validation.streetNumber.pattern}")
    private String streetNumber;

    /** Apartment or suite number (maximum 6 characters). */
    @Size(max = 6, message = "{constraint.string.length.max}")
    @Pattern(regexp = "^$|^[0-9A-Za-z\\s/-]+$", message = "{validation.homeNumber.pattern}")
    private String homeNumber;

    /** Postal code in XX-XXX format. */
    @Size(max = 6, message = "{constraint.string.length.max}")
    @Pattern(regexp = "^$|^\\d{2}-\\d{3}$", message = "{validation.postCode.pattern}")
    private String postCode;

    /** City or town name (maximum 50 characters). */
    @Size(max = 50, message = "{constraint.string.length.max}")
    @Pattern(regexp = "^$|^[\\p{L}\\s\\.-]+$", message = "{validation.city.pattern}")
    private String city;

    /**
     * Creates an initialized DTO with empty strings to prevent null pointer issues in views.
     * @return a new AddressDto instance with empty fields
     */
    public static AddressDto empty() {
        return new AddressDto("", "", "", "", "");
    }
}