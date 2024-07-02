package com.lorby.auth_project.exception;

import com.lorby.auth_project.entity.User;

public class UsernameExistsException extends RuntimeException{
    public UsernameExistsException(String message){
        super(message);
    }
}
