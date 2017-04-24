package com.backbase.kalah.exception;

/**
 * Exception class to be used when Board is not found in store
 * Created by tojagrut
 */
public class BoardNotFoundException extends RuntimeException {

    /**
     * Instantiate exception with error message
     * @param errorMessage errorMessage
     */
    public BoardNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
