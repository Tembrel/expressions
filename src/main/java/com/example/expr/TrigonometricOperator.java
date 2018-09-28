package com.example.expr;

import static com.example.expr.Expression.expr;

import java.util.function.DoubleUnaryOperator;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperator implements UnaryOperator {
    SINE(Math::sin, "sin(%s)"),
    COSINE(Math::cos, "cos(%s)"),
    TANGENT(Math::tan, "tan(%s)"),
    ARC_SINE(Math::asin, "asin(%s)"),
    ARC_COSINE(Math::acos, "acos(%s)"),
    ARC_TANGENT(Math::atan, "atan(%s)"),

    ;

    final DoubleUnaryOperator eval;
    final String fmt;

    TrigonometricOperator(DoubleUnaryOperator eval, String fmt) {
        this.eval = eval;
        this.fmt = fmt;
    }

    @Override public int precedence() { return 12; }

    @Override public double evaluate(double v) { return eval.applyAsDouble(v); }
    @Override public String format(String s) { return String.format(fmt, s); }


    public static Expression sin(Expression expr) {
        return new UnaryOperationExpression(SINE, expr);
    }

    public static Expression cos(Expression expr) {
        return new UnaryOperationExpression(COSINE, expr);
    }

    public static Expression tan(Expression expr) {
        return new UnaryOperationExpression(TANGENT, expr);
    }

    public static Expression asin(Expression expr) {
        return new UnaryOperationExpression(ARC_SINE, expr);
    }

    public static Expression acos(Expression expr) {
        return new UnaryOperationExpression(ARC_COSINE, expr);
    }

    public static Expression atan(Expression expr) {
        return new UnaryOperationExpression(ARC_TANGENT, expr);
    }



    public static Expression sin(double val) {
        return sin(expr(val));
    }

    public static Expression cos(double val) {
        return cos(expr(val));
    }

    public static Expression tan(double val) {
        return tan(expr(val));
    }

    public static Expression asin(double val) {
        return asin(expr(val));
    }

    public static Expression acos(double val) {
        return acos(expr(val));
    }

    public static Expression atan(double val) {
        return atan(expr(val));
    }


    public static Expression sin(String varName) {
        return sin(expr(varName));
    }

    public static Expression cos(String varName) {
        return cos(expr(varName));
    }

    public static Expression tan(String varName) {
        return tan(expr(varName));
    }

    public static Expression asin(String varName) {
        return asin(expr(varName));
    }

    public static Expression acos(String varName) {
        return acos(expr(varName));
    }

    public static Expression atan(String varName) {
        return atan(expr(varName));
    }
}
