package fr.afpa.pompey.APIEcommerce.exceptions;

/**
 * Runtime exception thrown when the order's status does not allow the operation to proceed.
 */
public class InvalidOrderStatusException extends RuntimeException {

    /**
     * @param message description of the conflict
     */
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
