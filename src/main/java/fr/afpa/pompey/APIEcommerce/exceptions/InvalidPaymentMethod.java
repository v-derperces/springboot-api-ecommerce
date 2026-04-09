package fr.afpa.pompey.APIEcommerce.exceptions;

/**
 * Runtime exception thrown when a payment method is not allowed.
 */
public class InvalidPaymentMethod extends RuntimeException {

    /**
     * @param message description of the problem
     */
    public InvalidPaymentMethod(String message) {
        super(message);
    }
}
