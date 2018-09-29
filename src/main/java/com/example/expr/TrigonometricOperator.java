package com.example.expr;

import static com.example.expr.Operator.Associativity;
import static com.example.expr.Operator.Fixity;
import static com.example.expr.OperatorBuilder.unary;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperator implements UnaryOperator {

    SINE        (unary("sin ", Math::sin)       .precedence(15)),
    COSINE      (unary("cos ", Math::cos)       .precedence(15)),
    TANGENT     (unary("tan ", Math::tan)       .precedence(15)),
    ARC_SINE    (unary("asin ", Math::asin)     .precedence(15)),
    ARC_COSINE  (unary("acos ", Math::acos)     .precedence(15)),
    ARC_TANGENT (unary("atan ", Math::atan)     .precedence(15)),

    ;

    UnaryOperator delegate;

    TrigonometricOperator(UnaryOperator delegate) {
        this.delegate = delegate;
    }

    @Override public int precedence() { return delegate.precedence(); }
    @Override public Fixity fixity() { return delegate.fixity(); }
    @Override public Associativity associativity() { return delegate.associativity(); }

    @Override public double evaluate(double v) { return delegate.evaluate(v); }
    @Override public String format(String s) { return delegate.format(s); }


    public static UnaryOperationExpression sin(Expression expr) {
        return new UnaryOperationExpression(SINE, expr);
    }

    public static UnaryOperationExpression cos(Expression expr) {
        return new UnaryOperationExpression(COSINE, expr);
    }

    public static UnaryOperationExpression tan(Expression expr) {
        return new UnaryOperationExpression(TANGENT, expr);
    }

    public static UnaryOperationExpression asin(Expression expr) {
        return new UnaryOperationExpression(ARC_SINE, expr);
    }

    public static UnaryOperationExpression acos(Expression expr) {
        return new UnaryOperationExpression(ARC_COSINE, expr);
    }

    public static UnaryOperationExpression atan(Expression expr) {
        return new UnaryOperationExpression(ARC_TANGENT, expr);
    }
}
