package com.example.bibliotheque.exception;

public class EmpruntImpossibleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmpruntImpossibleException(String message) {
        super(message);
    }
}
