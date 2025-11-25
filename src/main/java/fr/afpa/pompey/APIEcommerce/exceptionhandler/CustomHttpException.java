package fr.afpa.pompey.APIEcommerce.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomHttpException extends Exception {

    private int statusCode = 400;
    private String reasonPhrase;

    public CustomHttpException(String message, int status, String reasonPhrase) {
        super(message);
        this.statusCode = status;
        this.reasonPhrase = reasonPhrase;
    }

}
