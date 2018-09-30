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
