package com.example.expr;

/**
 * Thrown a bound variable is used in its own binding expression.
 */
public class SelfReferenceException extends BindingException {
    SelfReferenceException() {
        super("Cannot bind variable to subexpression that refers to it");
    }
}
