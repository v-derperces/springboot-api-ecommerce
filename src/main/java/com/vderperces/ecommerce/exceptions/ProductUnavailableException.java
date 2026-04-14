package com.vderperces.ecommerce.exceptions;

/**
 * Runtime exception thrown when a product purchased is unavailable for purchase.
 */
public class ProductUnavailableException extends RuntimeException {

    /**
     * @param message description of the conflict
     */
    public ProductUnavailableException(String message) {
        super(message);
    }
}
