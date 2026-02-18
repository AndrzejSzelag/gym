package pl.szelag.gym.client.entity;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.common.api.AddressProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressTest {

    @Test
    void shouldUpdateAddressWithSanitization() {
        // GIVEN
        AddressProvider provider = mock(AddressProvider.class);
        when(provider.getStreet()).thenReturn("  Makowa  ");
        when(provider.getStreetNumber()).thenReturn(" 10A ");
        when(provider.getPostCode()).thenReturn(" 62-510 ");
        when(provider.getCity()).thenReturn(" Konin ");

        // WHEN
        Address address = new Address(provider);

        // THEN
        assertThat(address.getStreet()).isEqualTo("Makowa");
        assertThat(address.getCity()).isEqualTo("Konin");
        assertThat(address.getPostCode()).isEqualTo("62-510");
    }

    @Test
    void shouldHandleNullProviderInConstructorAndIgnoreUpdate() {
        // GIVEN
        Address address = new Address(mock(AddressProvider.class));

        // WHEN
        address.update(null);

        // THEN
        // Verify that the address fields remain unchanged (null in this case)
        assertThat(address.getStreet()).isNull();
    }
}