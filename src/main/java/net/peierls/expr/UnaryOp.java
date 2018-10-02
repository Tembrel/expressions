package net.peierls.expr;

/**
 * A double precision unary operator, supporting evaluation and
 * formatting (string representation).
 */
public interface UnaryOp extends Operator {

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
