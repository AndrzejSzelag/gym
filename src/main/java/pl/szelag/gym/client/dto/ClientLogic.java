package pl.szelag.gym.client.dto;

import pl.szelag.gym.common.api.AddressProvider;
import pl.szelag.gym.common.api.MembershipStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Common logic for client data structures, providing consistent UI properties. */
public interface ClientLogic {
    /** client's first name */
    String getFirstName();

    /** client's last name */
    String getLastName();

    /** membership expiration date, or null if not assigned */
    LocalDate getExpirationDate();

    /** address provider instance */
    AddressProvider getAddress();

    /** formatted address string, "No address", or "Incomplete address" */
    default String getFullAddress() {
        AddressProvider addressProvider = getAddress();
        if (addressProvider == null) return "No address";
        String formatted = addressProvider.getFullAddress();
        return (formatted != null && !formatted.isBlank()) ? formatted : "Incomplete address";
    }

    /** full name or empty string if names are null */
    default String getFullName() {
        String fName = getFirstName() != null ? getFirstName().trim() : "";
        String lName = getLastName() != null ? getLastName().trim() : "";
        return (fName + " " + lName).trim();
    }

    /** true if expiration date is not null */
    default boolean hasAssignedSubscription() {
        return getExpirationDate() != null;
    }

    /** true if subscription exists and is not expired */
    default boolean hasActiveSubscription() {
        return getExpirationDate() != null && !getExpirationDate().isBefore(LocalDate.now());
    }

    /** calculated {@link MembershipStatus} */
    default MembershipStatus getMembershipStatus() {
        if (!hasAssignedSubscription()) return MembershipStatus.NOT_ASSIGNED;
        return hasActiveSubscription() ? MembershipStatus.ACTIVE : MembershipStatus.EXPIRED;
    }

    /** number of days until (pos) or since (neg) expiration, or null if not set */
    default Long daysUntilOrSinceExpiration() {
        if (getExpirationDate() == null) return null;
        return ChronoUnit.DAYS.between(LocalDate.now(), getExpirationDate());
    }
}