package com.iag.reactive.exceptions;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceException extends RuntimeException{

    private String message;
    private Integer statusCode;


}
