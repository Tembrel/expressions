package com.example.expr;

import static com.example.expr.Operator.Fixity.POSTFIX;

import static com.example.expr.OperatorBuilder.binary;
import static com.example.expr.OperatorBuilder.unary;


/**
 * Logarithmic and exponential (power) operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicOperator implements UnaryOperator, BinaryOperator {

    NEGATED     (unary("-", a -> -a)                     .precedence(14)),
    SQUARED     (unary("^2", a -> a * a).fixity(POSTFIX) .precedence(15)),
    SQUARE_ROOT (unary("sqrt ", Math::sqrt)              .precedence(15)),

    PLUS        (binary(" + ", (a, b) -> a + b)          .precedence(11)),
    MINUS       (binary(" - ", (a, b) -> a - b)          .precedence(11)),
    TIMES       (binary(" ", (a, b) -> a * b)            .precedence(13)),
    DIVIDED_BY  (binary(" / ", (a, b) -> divide(a, b))   .precedence(12)),

    ;

    Operator delegate;

    BasicOperator(Operator delegate) {
        this.delegate = delegate;
    }

    UnaryOperator unaryOp() {
        if (delegate.arity() == 1) {
            return ((UnaryOperator) delegate);
        }
        throw new ClassCastException("Attempt to evaluate binary operator as unary operator");
    }

    BinaryOperator binaryOp() {
        if (delegate.arity() == 2) {
            return ((BinaryOperator) delegate);
        }
        throw new ClassCastException("Attempt to evaluate unary operator as binary operator");
    }


    @Override public int precedence() { return delegate.precedence(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public Associativity associativity() { return delegate.associativity(); }

    @Override public double evaluate(double v) { return unaryOp().evaluate(v); }
    @Override public String format(String s) { return unaryOp().format(s); }

    @Override public double evaluate(double v1, double v2) { return binaryOp().evaluate(v1, v2); }
    @Override public String format(String s1, String s2) { return binaryOp().format(s1, s2); }

    private static double divide(double a, double b) {
        double c = a / b;
        if (Double.isFinite(c)) {
            return c;
        }
        throw new DivisionByZeroException(a, b);
    }
}
