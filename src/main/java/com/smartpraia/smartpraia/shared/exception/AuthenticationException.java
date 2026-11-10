package com.smartpraia.smartpraia.shared.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String mensagem){
        super(mensagem);
    }

}
