package com.iag.reactive.exceptions;

public class PersonDetailsClientException extends ServiceException {

    public PersonDetailsClientException(String message, Integer statusCode) {
        super(message, statusCode);
    }
}
