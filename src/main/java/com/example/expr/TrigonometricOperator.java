package com.example.expr;

import static com.example.expr.Operator.Associativity;
import static com.example.expr.Operator.Fixity;
import static com.example.expr.OperatorBuilder.op;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperator implements DelegatingOp {

    SINE        (op("sin ", Math::sin)       .precedence(15)),
    COSINE      (op("cos ", Math::cos)       .precedence(15)),
    TANGENT     (op("tan ", Math::tan)       .precedence(15)),
    ARC_SINE    (op("asin ", Math::asin)     .precedence(15)),
    ARC_COSINE  (op("acos ", Math::acos)     .precedence(15)),
    ARC_TANGENT (op("atan ", Math::atan)     .precedence(15)),

    ;

    Operator delegate;

    TrigonometricOperator(Operator delegate) {
        this.delegate = delegate;
    }

    @Override public Operator delegate() { return delegate; }


    public static UnaryOpExpression sin(Expression expr) {
        return new UnaryOpExpression(SINE, expr);
    }

    public static UnaryOpExpression cos(Expression expr) {
        return new UnaryOpExpression(COSINE, expr);
    }

    public static UnaryOpExpression tan(Expression expr) {
        return new UnaryOpExpression(TANGENT, expr);
    }

    public static UnaryOpExpression asin(Expression expr) {
        return new UnaryOpExpression(ARC_SINE, expr);
    }

    public static UnaryOpExpression acos(Expression expr) {
        return new UnaryOpExpression(ARC_COSINE, expr);
    }

    public static UnaryOpExpression atan(Expression expr) {
        return new UnaryOpExpression(ARC_TANGENT, expr);
    }
}
