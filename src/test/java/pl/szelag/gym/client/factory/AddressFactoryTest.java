package pl.szelag.gym.client.factory;

import org.junit.jupiter.api.Test;
import pl.szelag.gym.client.entity.Address;
import pl.szelag.gym.common.api.AddressProvider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressFactoryTest {

    @Test
    void shouldThrowExceptionWhenProviderIsNull() {
        // WHEN & THEN
        assertThatThrownBy(() -> AddressFactory.fromProvider(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("AddressProvider must not be null");
    }

    @Test
    void shouldReturnNullWhenAllProviderFieldsAreEmpty() {
        // GIVEN
        AddressProvider provider = mock(AddressProvider.class);
        when(provider.getStreet()).thenReturn("");
        when(provider.getStreetNumber()).thenReturn("  ");
        when(provider.getHomeNumber()).thenReturn(null);
        when(provider.getPostCode()).thenReturn(null);
        when(provider.getCity()).thenReturn("");

        // WHEN
        Address result = AddressFactory.fromProvider(provider);

        // THEN
        assertThat(result).isNull();
    }

    @Test
    void shouldCreateAddressWhenAtLeastOneFieldIsPresent() {
        // GIVEN
        AddressProvider provider = mock(AddressProvider.class);
        when(provider.getStreet()).thenReturn(" Main St ");
        when(provider.getStreetNumber()).thenReturn(null);
        when(provider.getHomeNumber()).thenReturn(null);
        when(provider.getPostCode()).thenReturn(null);
        when(provider.getCity()).thenReturn(null);

        // WHEN
        Address result = AddressFactory.fromProvider(provider);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo("Main St");
    }
}