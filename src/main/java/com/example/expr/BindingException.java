package com.example.expr;

/**
 * Base for exceptions thrown during expression evaluation.
 */
public class BindingException extends RuntimeException {
    protected BindingException(String message) {
        super(message);
    }
}