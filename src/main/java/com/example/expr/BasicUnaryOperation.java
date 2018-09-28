package com.example.expr;

import java.util.function.DoubleUnaryOperator;


/**
 * Basic unary operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicUnaryOperation implements UnaryOperation {
    NEGATED(a -> -a, "-%s"),
    SQUARED(a -> a * a, "%s^2"),
    SQUARE_ROOT(Math::sqrt, "sqrt(%s)"),
    ;
    final DoubleUnaryOperator op;
    final String fmt;
    BasicUnaryOperation(DoubleUnaryOperator op, String fmt) {
        this.op = op;
        this.fmt = fmt;
    }
    @Override public double evaluate(double v) { return op.applyAsDouble(v); }
    @Override public String format(String s) { return String.format(fmt, s); }
}
