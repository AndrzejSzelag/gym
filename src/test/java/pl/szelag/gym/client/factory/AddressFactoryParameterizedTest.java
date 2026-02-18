package pl.szelag.gym.client.factory;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.szelag.gym.client.entity.Address;
import pl.szelag.gym.common.api.AddressProvider;
import pl.szelag.gym.utility.StringUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressFactoryParameterizedTest {

    /**
     * Provides test scenarios: some fields empty or null, some meaningful.
     * Expected indicates whether Address should be created.
     */
    static Stream<TestCase> provideScenarios() {
        return Stream.of(
                new TestCase(null, null, null, null, null, false),
                new TestCase("", "  ", null, "", "", false),
                new TestCase("null", "NULL", "  ", null, "", false),
                new TestCase(" Main St ", null, null, null, null, true),
                new TestCase(null, "10", null, null, null, true),
                new TestCase(null, null, "5", null, null, true),
                new TestCase(null, null, null, "00-001", null, true),
                new TestCase(null, null, null, null, " Konin ", true),
                new TestCase(" Main St ", "10", "5", "00-001", " Konin ", true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideScenarios")
    void shouldCreateAddressOnlyWhenMeaningfulDataExists(TestCase tc) {
        // GIVEN
        AddressProvider provider = mock(AddressProvider.class);
        when(provider.getStreet()).thenReturn(tc.street);
        when(provider.getStreetNumber()).thenReturn(tc.streetNumber);
        when(provider.getHomeNumber()).thenReturn(tc.homeNumber);
        when(provider.getPostCode()).thenReturn(tc.postCode);
        when(provider.getCity()).thenReturn(tc.city);

        // WHEN
        Address result = AddressFactory.fromProvider(provider);

        // THEN
        if (tc.shouldExist) {
            assertThat(result).isNotNull();

            // Check sanitization
            assertThat(result.getStreet()).isEqualTo(StringUtils.sanitize(tc.street));
            assertThat(result.getStreetNumber()).isEqualTo(StringUtils.sanitize(tc.streetNumber));
            assertThat(result.getHomeNumber()).isEqualTo(StringUtils.sanitize(tc.homeNumber));
            assertThat(result.getPostCode()).isEqualTo(StringUtils.sanitize(tc.postCode));
            assertThat(result.getCity()).isEqualTo(StringUtils.sanitize(tc.city));
        } else {
            assertThat(result).isNull();
        }
    }

    private record TestCase(String street, String streetNumber, String homeNumber,
                            String postCode, String city, boolean shouldExist) {}
}