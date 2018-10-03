package net.peierls.expr;

/**
 * A double precision binary operator, supporting evaluation and
 * formatting (string representation).
 */
public interface BinaryOp extends Operator {

    /**
     * Apply this operation to two double values.
     */
    double evaluate(double v1, double v2);

    /**
     * Find the string representation of this operation given a string
     * representation of its arguments.
     */
    String format(String s1, String s2);
}
