package com.lorby.auth_project.exception;

import jakarta.validation.constraints.Email;

public class EmailExistsException extends RuntimeException{
    public EmailExistsException(String message){
        super(message);
    }
}
