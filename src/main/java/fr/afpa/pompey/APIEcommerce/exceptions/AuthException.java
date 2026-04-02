package fr.afpa.pompey.APIEcommerce.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Runtime exception indicating an authentication/authorization failure.
 */
public class AuthException extends RuntimeException {

    /** HTTP status code for the auth failure. */
    private final HttpStatus status;

    /**
     * Create exception with message and status.
     *
     * @param message user-visible error message
     * @param status HTTP status code for the failure
     */
    public AuthException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Returns the HTTP status associated with this exception.
     *
     * @return the HTTP status
     */
    public HttpStatus getStatus() {
        return status;
    }

}
