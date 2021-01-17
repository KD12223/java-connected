package com.kylerdeggs.javaconnected.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Handles all exceptions for the controllers.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HttpResponse> return400(IllegalArgumentException exception) {
        return new ResponseEntity<>(new HttpResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<HttpResponse> return403(SecurityException exception) {
        return new ResponseEntity<>(new HttpResponse(HttpStatus.FORBIDDEN.getReasonPhrase(),
                exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<HttpResponse> return404(NoSuchElementException exception) {
        return new ResponseEntity<>(new HttpResponse(HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<HttpResponse> return415(UnsupportedOperationException exception) {
        return new ResponseEntity<>(new HttpResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
                exception.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> return500(IOException exception) {
        return new ResponseEntity<>(new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
