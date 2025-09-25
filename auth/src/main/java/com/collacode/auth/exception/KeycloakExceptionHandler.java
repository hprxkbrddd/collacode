package com.collacode.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class KeycloakExceptionHandler {

    @ExceptionHandler(KeycloakException.class)
    public void handleKeycloakException(KeycloakException ex){
        System.out.println("Keycloak error: "+ex.getMessage());
    }
}
