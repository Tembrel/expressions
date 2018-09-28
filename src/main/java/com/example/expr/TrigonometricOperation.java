package com.example.expr;

import java.util.function.DoubleUnaryOperator;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperation implements UnaryOperation {
    SINE(Math::sin, "sin(%s)"),
    COSINE(Math::cos, "cos(%s)"),
    TANGENT(Math::tan, "tan(%s)"),
    ARC_SINE(Math::asin, "asin(%s)"),
    ARC_COSINE(Math::acos, "acos(%s)"),
    ARC_TANGENT(Math::atan, "atan(%s)"),

    ;

    final DoubleUnaryOperator eval;
    final String fmt;
    TrigonometricOperation(DoubleUnaryOperator eval, String fmt) {
        this.eval = eval;
        this.fmt = fmt;
    }
    @Override public double evaluate(double v) { return eval.applyAsDouble(v); }
    @Override public String format(String s) { return String.format(fmt, s); }
}
