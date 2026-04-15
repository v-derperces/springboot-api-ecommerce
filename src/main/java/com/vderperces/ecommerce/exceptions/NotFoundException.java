package com.vderperces.ecommerce.exceptions;

/**
 * Runtime exception thrown when a requested resource is not found.
 */
public class NotFoundException extends RuntimeException {

    /**
     * @param message description of resource not found
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
