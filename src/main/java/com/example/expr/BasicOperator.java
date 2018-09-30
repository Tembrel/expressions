package com.example.expr;

import static com.example.expr.OperatorBuilder.*;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicOperator implements DelegatingOp {

    NEGATED     (prefix("-", a -> -a)                   .precedence(100)),
    SQUARED     (postfix("^2", a -> a * a)              .precedence(100)),
    SQUARE_ROOT (prefix("sqrt ", Math::sqrt)            .precedence(100)),

    PLUS        (infixl(" + ", (a, b) -> a + b)         .precedence(10)),
    MINUS       (infixl(" - ", (a, b) -> a - b)         .precedence(10)),
    TIMES       (infixl(" ", (a, b) -> a * b)           .precedence(20)),
    DIVIDED_BY  (infixl(" / ", (a, b) -> divide(a, b))  .precedence(20)),

    ;

    Operator delegate;

    BasicOperator(Operator delegate) {
        this.delegate = delegate;
    }

    @Override public Operator delegate() { return delegate; }

    @Override public String format(String s1, String s2) {
        /*
        if (this == TIMES) {
            if (s1.matches("(^|[-A-Za-z0-9])[0-9]+") && !s2.matches("^[0-9]")) {
                return s1 + s2;
            }
        }
        */
        return DelegatingOp.super.format(s1, s2);
    }

    private static double divide(double a, double b) {
        double c = a / b;
        if (Double.isFinite(c)) {
            return c;
        }
        throw new DivisionByZeroException(a, b);
    }
}
