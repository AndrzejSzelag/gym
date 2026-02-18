package pl.szelag.gym.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/** Base contract for domain-specific exceptions, supporting i18n and HTTP status mapping. */
public abstract class GymException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** @param messageKey localization key from message bundles */
    protected GymException(String messageKey) {
        super(messageKey);
    }

    /** HTTP status associated with this specific business error */
    public abstract HttpStatus getHttpStatus();

    /** dynamic arguments for i18n; defaults to empty array to ensure null-safety */
    public Object[] getArgs() {
        return new Object[0];
    }
}