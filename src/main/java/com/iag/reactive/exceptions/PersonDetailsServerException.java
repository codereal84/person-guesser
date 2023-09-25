package com.iag.reactive.exceptions;

public class PersonDetailsServerException extends  ServiceException {
    public PersonDetailsServerException(String message, Integer statusCode) {
        super(message, statusCode);
    }
}
