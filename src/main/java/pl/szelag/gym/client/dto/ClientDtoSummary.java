package pl.szelag.gym.client.dto;

import pl.szelag.gym.common.api.AddressProvider;

import java.time.LocalDate;

/** Interface-based projection for optimized client list views. */
public interface ClientDtoSummary extends ClientLogic {

    /** unique identifier of the client */
    Long getId();

    /** client's first name */
    String getFirstName();

    /** client's last name */
    String getLastName();

    /** client's unique email address */
    String getEmail();

    /** client's contact phone number */
    String getPhone();

    /** date of initial registration */
    LocalDate getRegistrationDate();

    /** membership expiration date or null if not active */
    LocalDate getExpirationDate();

    /** nested projection of address details */
    AddressSummary getAddress();

    /** Nested projection for streamlined address data access. */
    interface AddressSummary extends AddressProvider {
        /** street name */
        String getStreet();

        /** building number */
        String getStreetNumber();

        /** apartment number or null if not applicable */
        String getHomeNumber();

        /** regional postal code */
        String getPostCode();

        /** city or town name */
        String getCity();
    }
}