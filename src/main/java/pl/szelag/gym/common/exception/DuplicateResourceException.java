package pl.szelag.gym.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/** Thrown when an operation violates a unique business constraint (e.g., duplicate email). */
@Getter
public class DuplicateResourceException extends GymException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** name of the resource type that caused the conflict */
    private final String resourceType;

    /** message arguments for internationalization */
    private final transient Object[] args;

    /** @param resourceType name of resource @param messageKey i18n key @param args formatting arguments */
    public DuplicateResourceException(String resourceType, String messageKey, Object... args) {
        super(messageKey);
        this.resourceType = resourceType;
        this.args = args;
    }

    /** HTTP 409 Conflict status */
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}