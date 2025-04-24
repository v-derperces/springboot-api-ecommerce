package fr.afpa.pompey.APIBoulangerie.exceptionhandler;

public class DuplicationException extends RuntimeException {
    public DuplicationException(String message) {
        super(message);
    }
}
