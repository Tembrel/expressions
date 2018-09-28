package com.example.expr;

/**
 * Thrown when attempting to bind a variable that is not referenced by a subexpression.
 */
public class UnreferencedVariableException extends BindingException {
    UnreferencedVariableException() {
        super("Trying to bind variable that is not referred to by subexpression");
    }
}
