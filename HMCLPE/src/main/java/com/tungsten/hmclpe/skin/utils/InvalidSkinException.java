package com.tungsten.hmclpe.skin.utils;

public class InvalidSkinException extends Exception {

    public InvalidSkinException() {}

    public InvalidSkinException(String message) {
        super(message);
    }

    public InvalidSkinException(Throwable cause) {
        super(cause);
    }

    public InvalidSkinException(String message, Throwable cause) {
        super(message, cause);
    }
}