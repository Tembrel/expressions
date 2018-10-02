package net.peierls.expr;

import static net.peierls.expr.OperatorBuilder.*;


/**
 * Basic double-precision arithmetic operators.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicOperator implements DelegatingOp {

    /** Negation operator */
    NEGATED     (prefix("-", a -> -a)                   .precedence(100)),

    /** Square operator */
    SQUARED     (postfix("^2", a -> a * a)              .precedence(100)),

    /** Square root operator */
    SQUARE_ROOT (prefix("sqrt ", Math::sqrt)            .precedence(100)),

    /** Addition operator */
    PLUS        (infixl(" + ", (a, b) -> a + b)         .precedence(10)),

    /** Subtraction operator */
    MINUS       (infixl(" - ", (a, b) -> a - b)         .precedence(10)),

    /**
     * Multiplication operator, formatted as whitespace,
     * parsed as either star or whitespace.
     */
    TIMES       (infixl(" * ", (a, b) -> a * b)         .precedence(21)) {
        @Override public String format(String s1, String s2) {
            return String.format("%s %s", s1, s2);
        }
    },

    /** Division operator */
    DIVIDED_BY  (infixl(" / ", (a, b) -> divide(a, b))  .precedence(20)),

    /** Natural logarithm operator */
    NATURAL_LOG (prefix("ln ", Math::log)               .precedence(100)),

    /** Logarithm base 10 operator */
    LOG_BASE_10 (prefix("log ", Math::log10)            .precedence(100)),

    /** e^x operator */
    EXP         (prefix("exp ", Math::exp)              .precedence(30)),

    /** Power (x^y) operator */
    POW         (infixr("^", Math::pow)                 .precedence(30)),

    ;

    final Operator delegate;
    BasicOperator(Operator delegate) { this.delegate = delegate; }
    @Override public Operator delegate() { return delegate; }


    /** Helper for division operator */
    private static double divide(double a, double b) {
        double c = a / b;
        if (Double.isFinite(c)) {
            return c;
        }
        throw new DivisionByZeroException(a, b);
    }
}
