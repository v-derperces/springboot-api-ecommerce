package fr.afpa.pompey.APIEcommerce.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<CustomError> handleHttpException(CustomHttpException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                new CustomError(
                        ex.getStatusCode(),
                        ex.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ),
                HttpStatus.valueOf(ex.getStatusCode())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                sb.append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );

        return new ResponseEntity<>(
                new CustomError(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        sb.toString(),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
