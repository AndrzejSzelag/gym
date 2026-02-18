package pl.szelag.gym.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CommonExceptionsTest {

    @Test
    void shouldHandleDuplicateResourceException() {
        // GIVEN
        DuplicateResourceException ex = new DuplicateResourceException("CLIENT", "key", "arg");

        // WHEN & THEN
        assertThat(ex.getResourceType()).isEqualTo("CLIENT");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getArgs()).containsExactly("arg");
    }

    @Test
    void shouldHandleInvalidDataException() {
        // GIVEN
        InvalidDataException ex = new InvalidDataException("key", "arg");

        // WHEN & THEN
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getArgs()).containsExactly("arg");
    }

    @Test
    void shouldHandleNullArgsInInvalidData() {
        // GIVEN
        InvalidDataException ex = new InvalidDataException("key", (Object[]) null);

        // WHEN & THEN
        assertThat(ex.getArgs()).isNotNull();
        assertThat(ex.getArgs()).isEmpty();
    }

    @Test
    void shouldThrowResourceNotFoundException() {
        // GIVEN
        String message = "Not found";

        // WHEN & THEN
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> {
                    throw new ResourceNotFoundException(message);
                }).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(message);
    }
}