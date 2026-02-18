package pl.szelag.gym.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @Test
    void givenWhiteSpacedAndUppercaseInput_whenNormalizing_thenReturnsCleanLowercaseString() {
        // GIVEN
        String input = "  ANDRZEJ.SZELAG@GYM.PL  ";

        // WHEN
        String result = StringUtils.normalize(input);

        // THEN
        assertThat(result).isEqualTo("andrzej.szelag@gym.pl");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "null", "NULL", "nUlL"})
    void givenBlankOrNullLikeString_whenSanitizing_thenReturnsActualNull(String input) {
        // WHEN
        String result = StringUtils.sanitize(input);

        // THEN
        assertThat(result).isNull();
    }

    @Test
    void givenDirtyName_whenSanitizing_thenReturnsTrimmedProperString() {
        // GIVEN
        String input = "   Andrzej   ";

        // WHEN
        String result = StringUtils.sanitize(input);

        // THEN
        assertThat(result).isEqualTo("Andrzej");
    }

    @ParameterizedTest
    @CsvSource({
            "  ANDRZEJ , andrzej",
            "Szelag   , szelag",
            "GYM.PL, gym.pl"
    })
    void givenVariousInputs_whenNormalizing_thenCorrectsCaseAndWhitespace(String input, String expected) {
        // WHEN
        String result = StringUtils.normalize(input);

        // THEN
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void givenNullReference_whenNormalizing_thenReturnsNullWithoutException() {
        // GIVEN
        String input = null;

        // WHEN
        String result = StringUtils.normalize(input);

        // THEN
        assertThat(result).isNull();
    }
}