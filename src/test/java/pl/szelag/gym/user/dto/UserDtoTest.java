package pl.szelag.gym.user.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void givenBuilderValues_whenBuildingDto_thenAllFieldsAreCorrectlyPopulated() {
        // GIVEN
        String firstName = "Andrzej";
        String lastName = "Szelag";
        String email = "andrzej.szelag@gym.pl";
        String password = "encoded_pass";

        // WHEN
        UserDto dto = UserDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .build();

        // THEN
        assertThat(dto.firstName()).isEqualTo(firstName);
        assertThat(dto.lastName()).isEqualTo(lastName);
        assertThat(dto.email()).isEqualTo(email);
        assertThat(dto.password()).isEqualTo(password);
    }

    @ParameterizedTest
    @CsvSource({
            "Andrzej, Szelag, Andrzej Szelag",
            " Andrzej, Szelag , Andrzej Szelag",
            "Andrzej, '', Andrzej",
            "Andrzej, , Andrzej"
    })
    void givenDifferentNameCombinations_whenGettingFullName_thenReturnsCorrectlyFormattedString(
            String firstName, String lastName, String expectedFullName) {
        // GIVEN
        UserDto dto = new UserDto(firstName, lastName, "andrzej.szelag@gym.pl", "pass");

        // WHEN
        String result = dto.getFullName();

        // THEN
        assertThat(result).isEqualTo(expectedFullName);
    }

    @Test
    void givenEmptyFactory_whenCreatingDto_thenAllFieldsAreEmptyStrings() {
        // WHEN
        UserDto dto = UserDto.empty();

        // THEN
        assertThat(dto.firstName()).isEmpty();
        assertThat(dto.lastName()).isEmpty();
        assertThat(dto.email()).isEmpty();
        assertThat(dto.password()).isEmpty();
    }

    @Test
    void givenUserDto_whenCheckingType_thenItIsRecognizedAsJavaRecord() {
        // GIVEN
        UserDto dto = new UserDto("Andrzej", "Szelag", "andrzej.szelag@gym.pl", "pass");

        // WHEN & THEN
        assertThat(dto.getClass().isRecord()).isTrue();
        assertThat(dto).isInstanceOf(Record.class);
    }
}