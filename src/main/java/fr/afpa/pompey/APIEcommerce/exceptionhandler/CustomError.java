package fr.afpa.pompey.APIEcommerce.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CustomError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public CustomError() {}

    public CustomError(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

}
