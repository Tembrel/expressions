package com.example.expr;

import static com.example.expr.Operator.Associativity;
import static com.example.expr.Operator.Fixity;
import static com.example.expr.OperatorBuilder.op;


/**
 * Basic binary operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicBinaryOperator implements BinaryOperator {

    PLUS(op().precedence(11).binary(" + ", (a, b) -> a + b)),
    MINUS(op().precedence(11).binary(" - ", (a, b) -> a - b)),
    TIMES(op().precedence(13).binary(" ", (a, b) -> a * b)),
    DIVIDED_BY(op().precedence(12).binary(" / ", (a, b) -> divide(a, b))),

    ;

    BinaryOperator delegate;

    BasicBinaryOperator(BinaryOperator delegate) {
        this.delegate = delegate;
    }

    @Override public Associativity associativity() { return delegate.associativity(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public int precedence() { return delegate.precedence(); }
    @Override public double evaluate(double v1, double v2) { return delegate.evaluate(v1, v2); }
    @Override public String format(String s1, String s2) { return delegate.format(s1, s2); }

    private static double divide(double a, double b) {
        double c = a / b;
        if (Double.isFinite(c)) {
            return c;
        }
        throw new DivisionByZeroException(a, b);
    }
}
