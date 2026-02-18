package pl.szelag.gym.common.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressProviderTest {

    @Test
    void shouldFormatFullAddressWithHomeNumber() {
        // GIVEN
        AddressProvider provider = new TestAddress(
                "Witosa", "10", "5", "62-510", "Konin"
        );

        // WHEN
        String result = provider.getFullAddress();

        // THEN
        assertEquals("Witosa 10/5, 62-510 Konin", result);
    }

    @Test
    void shouldFormatAddressWithoutHomeNumber() {
        // GIVEN
        AddressProvider provider = new TestAddress(
                "Makowa", "1", null, "62-510", "Konin"
        );

        // WHEN
        String result = provider.getFullAddress();

        // THEN
        assertEquals("Makowa 1, 62-510 Konin", result);
    }

    @Test
    void shouldHandleMissingStreetButHavingCity() {
        // GIVEN
        AddressProvider provider = new TestAddress(
                null, null, null, "62-500", "Konin"
        );

        // WHEN
        String result = provider.getFullAddress();

        // THEN
        assertEquals("62-500 Konin", result);
    }

    @Test
    void shouldReturnDefaultMessageWhenAllFieldsAreNull() {
        // GIVEN
        AddressProvider provider = new TestAddress(null, null, null, null, null);

        // WHEN
        String result = provider.getFullAddress();

        // THEN
        assertEquals("No address data provided", result);
    }

    @Test
    void shouldHandleNullStringsAndBlankValues() {
        // GIVEN
        AddressProvider provider = new TestAddress(
                "null", "  ", "NULL", "62-510", "Konin"
        );

        // WHEN
        String result = provider.getFullAddress();

        // THEN
        // Street info is "null/blank", so only location part should remain
        assertEquals("62-510 Konin", result);
    }

    @Test
    void shouldFormatAddressWithOnlyStreetAndNumber() {
        // GIVEN
        AddressProvider provider = new TestAddress(
                "Witosa", "10", null, null, null
        );

        // WHEN
        String result = provider.getFullAddress();

        // THEN
        assertEquals("Witosa 10", result);
    }

    // Helper class for testing interface default methods
    private record TestAddress(
            String street,
            String streetNumber,
            String homeNumber,
            String postCode,
            String city
    ) implements AddressProvider {
        @Override public String getStreet() { return street; }
        @Override public String getStreetNumber() { return streetNumber; }
        @Override public String getHomeNumber() { return homeNumber; }
        @Override public String getPostCode() { return postCode; }
        @Override public String getCity() { return city; }
    }
}