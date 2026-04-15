package com.vderperces.ecommerce.exceptions;

/**
 * Runtime exception thrown when the quantity of a product exceeds stock limit.
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * @param message description of the conflict
     */
    public InsufficientStockException(String message) {
        super(message);
    }

}
