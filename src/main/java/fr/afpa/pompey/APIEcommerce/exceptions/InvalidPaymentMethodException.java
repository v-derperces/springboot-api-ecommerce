package fr.afpa.pompey.APIEcommerce.exceptions;

/**
 * Runtime exception thrown when a payment method is not allowed.
 */
public class InvalidPaymentMethodException extends RuntimeException {

    /**
     * @param message description of the problem
     */
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}
