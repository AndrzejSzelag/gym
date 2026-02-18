package pl.szelag.gym.client.factory;

import pl.szelag.gym.client.entity.Address;
import pl.szelag.gym.common.api.AddressProvider;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Factory for creating Address Value Objects.
 * Provides logic to return null if no meaningful address data exists in the provider.
 */
public final class AddressFactory {

    /** Private constructor to prevent instantiation of utility factory class. */
    private AddressFactory() {}

    /**
     * Creates an Address entity from the given provider.
     *
     * @param provider address data source (e.g., DTO or other entity)
     * @return a new Address instance, or null if the provider contains no valid data
     * @throws NullPointerException if provider is null
     */
    public static Address fromProvider(AddressProvider provider) {
        Objects.requireNonNull(provider, "AddressProvider must not be null");
        if (isEmpty(provider)) return null;
        return new Address(provider);
    }

    /**
     * Checks if all address fields in the provider are effectively empty.
     *
     * @param p the address provider to check
     * @return true if all fields are null, blank, or literal "null"
     */
    private static boolean isEmpty(AddressProvider p) {
        return Stream.of(p.getStreet(), p.getStreetNumber(), p.getHomeNumber(), p.getPostCode(), p.getCity()
        ).allMatch(v -> v == null || v.isBlank() || v.equalsIgnoreCase("null"));
    }
}