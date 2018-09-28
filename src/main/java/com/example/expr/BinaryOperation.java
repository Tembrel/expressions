package com.example.expr;

/**
 * A double precision binary operation, supporting evaluation and
 * formatting (string representation).
 * This is an extensible enum pattern interface, and should only be
 * implemented by enum types to ensure that equality can be tested
 * using identity.
 */
public interface BinaryOperation {
    /**
     * Apply this operation to a double value.
     */
    double evaluate(double v1, double v2);

    /**
     * Find the string representation of this operation given a string
     * representation of its arguments.
     */
    String format(String s1, String s2);
}
