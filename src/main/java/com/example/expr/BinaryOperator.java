package com.example.expr;

/**
 * A double precision binary operator, supporting evaluation and
 * formatting (string representation).
 */
public interface BinaryOperator extends Operator {

    /**
     * Binary operators have arity 2. Don't override this.
     */
    @Override default int arity() { return 2; }

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
