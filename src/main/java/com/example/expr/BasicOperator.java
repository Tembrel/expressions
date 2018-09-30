package com.example.expr;

import static com.example.expr.Operator.Fixity.POSTFIX;

import static com.example.expr.OperatorBuilder.op;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicOperator implements DelegatingOperator {

    NEGATED     (op("-", a -> -a)                      .precedence(14)),
    SQUARED     (op("^2", a -> a * a) .fixity(POSTFIX) .precedence(15)),
    SQUARE_ROOT (op("sqrt ", Math::sqrt)               .precedence(15)),

    PLUS        (op(" + ", (a, b) -> a + b)            .precedence(11)),
    MINUS       (op(" - ", (a, b) -> a - b)            .precedence(11)),
    TIMES       (op(" ", (a, b) -> a * b)              .precedence(13)),
    DIVIDED_BY  (op(" / ", (a, b) -> divide(a, b))     .precedence(12)),

    ;

    Operator delegate;

    BasicOperator(Operator delegate) {
        this.delegate = delegate;
    }

    @Override public Operator delegate() { return delegate; }

    @Override public String format(String s1, String s2) {
        if (this == TIMES) {
            if (s1.matches("(^|[-A-Za-z0-9])[0-9]+") && !s2.matches("^[0-9]")) {
                return s1 + s2;
            }
        }
        return DelegatingOperator.super.format(s1, s2);
    }

    private static double divide(double a, double b) {
        double c = a / b;
        if (Double.isFinite(c)) {
            return c;
        }
        throw new DivisionByZeroException(a, b);
    }
}
