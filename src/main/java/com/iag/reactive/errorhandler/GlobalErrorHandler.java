package com.iag.reactive.errorhandler;


import com.iag.reactive.exceptions.PersonDetailsClientException;
import com.iag.reactive.exceptions.PersonDetailsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler  {

    @ExceptionHandler(PersonDetailsClientException.class)
    public ResponseEntity<String> handlePersonDetailsClientError(PersonDetailsClientException ex) {
        log.error("Client Error: Not able to get all the person details with message {}",ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(PersonDetailsServerException.class)
    public ResponseEntity<String> handlePersonDetailsServerError(PersonDetailsServerException ex) {
        log.error("Server Error: Not able to get all the person details with message {}",ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }
}
