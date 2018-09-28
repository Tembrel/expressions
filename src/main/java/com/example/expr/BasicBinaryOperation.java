package com.example.expr;

import java.util.function.DoubleBinaryOperator;


/**
 * Basic binary operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum BasicBinaryOperation implements BinaryOperation {
    PLUS((a, b) -> a + b, "(%s + %s)"),
    MINUS((a, b) -> a - b, "(%s - %s)"),
    TIMES((a, b) -> a * b, "%s %s"),
    DIVIDED_BY((a, b) -> divide(a, b), "(%s / %s)"),
    ;
    final DoubleBinaryOperator op;
    final String fmt;
    BasicBinaryOperation(DoubleBinaryOperator op, String fmt) {
        this.op = op;
        this.fmt = fmt;
    }
    @Override public double evaluate(double v1, double v2) { return op.applyAsDouble(v1, v2); }
    @Override public String format(String s1, String s2) { return String.format(fmt, s1, s2); }

    private static double divide(double a, double b) {
        double c = a / b;
        if (Double.isFinite(c)) {
            return c;
        }
        throw new DivisionByZeroException(a, b);
    }
}
