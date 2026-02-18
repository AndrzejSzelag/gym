package pl.szelag.gym.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.Objects;

/** Thrown when business validation fails or data is logically inconsistent. */
@Getter
public class InvalidDataException extends GymException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** message arguments for internationalization */
    private final transient Object[] args;

    /** @param messageKey i18n key @param args formatting arguments for the message */
    public InvalidDataException(String messageKey, Object... args) {
        super(messageKey);
        this.args = Objects.requireNonNullElse(args, new Object[0]);
    }

    /** HTTP 400 Bad Request status */
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    /** arguments array, guaranteed non-null */
    @Override
    public Object[] getArgs() {
        return args;
    }
}