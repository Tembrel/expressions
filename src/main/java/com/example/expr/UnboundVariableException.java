package com.example.expr;

/** Thrown when a variable is not bound to a value during evaluation */
public class UnboundVariableException extends EvaluationException {
    private final String varName;

    UnboundVariableException(String varName) {
        super(String.format("Unbound variable during evaluation: %s", varName));
        this.varName = varName;
    }

    public String unboundVariable() {
        return varName;
    }
}
