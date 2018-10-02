package net.peierls.expr;

import static net.peierls.expr.OperatorBuilder.*;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperator implements DelegatingOp {

    SINE        (prefix("sin ", Math::sin)      .precedence(100)),
    COSINE      (prefix("cos ", Math::cos)      .precedence(100)),
    TANGENT     (prefix("tan ", Math::tan)      .precedence(100)),
    ARC_SINE    (prefix("asin ", Math::asin)    .precedence(100)),
    ARC_COSINE  (prefix("acos ", Math::acos)    .precedence(100)),
    ARC_TANGENT (prefix("atan ", Math::atan)    .precedence(100)),

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
