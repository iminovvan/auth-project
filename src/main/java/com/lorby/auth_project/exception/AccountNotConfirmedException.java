package com.lorby.auth_project.exception;

public class AccountNotConfirmedException extends RuntimeException{
    public AccountNotConfirmedException(String message){
        super(message);
    }
}
