package com.example.expr;

/**
 * A double precision unary operation, supporting evaluation and
 * formatting (string representation).
 * This is an extensible enum pattern interface, and should only be
 * implemented by enum types to ensure that equality can be tested
 * using identity.
 */
public interface UnaryOperation {
    /**
     * Apply this operation to a double value.
     */
    double evaluate(double v);

    /**
     * Find the string representation of this operation given a string
     * representation of its argument.
     */
    String format(String s);
}
