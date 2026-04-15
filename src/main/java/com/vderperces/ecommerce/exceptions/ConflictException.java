package com.vderperces.ecommerce.exceptions;

/**
 * Runtime exception thrown when a request would cause a resource conflict.
 */
public class ConflictException extends RuntimeException {

    /**
     * @param message description of the conflict
     */
    public ConflictException(final String message) {
        super(message);
    }
}
