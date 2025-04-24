package fr.afpa.pompey.APIBoulangerie.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<CustomError> handleConflict(DuplicationException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                new CustomError(HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                        ),
                HttpStatus.CONFLICT
        );
    }

}
