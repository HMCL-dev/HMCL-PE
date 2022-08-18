package com.tungsten.hmclpe.auth;

public class CredentialExpiredException extends AuthenticationException {

    public CredentialExpiredException() {}

    public CredentialExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialExpiredException(String message) {
        super(message);
    }

    public CredentialExpiredException(Throwable cause) {
        super(cause);
    }
}