package com.vderperces.ecommerce.exceptions;

/**
 * Runtime exception thrown when the process of creation of an order cannot be completed.
 */
public class OrderCreationException extends RuntimeException {

    /**
     * @param message Description of the cause
     */
    public OrderCreationException(String message) {
        super(message);
    }
}
