package pl.szelag.gym.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/** Thrown when a requested domain resource cannot be found in the system. */
public class ResourceNotFoundException extends GymException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** @param messageKey i18n key describing which resource is missing */
    public ResourceNotFoundException(String messageKey) {
        super(messageKey);
    }

    /** HTTP 404 Not Found status */
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}