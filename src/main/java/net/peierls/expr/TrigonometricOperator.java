package net.peierls.expr;

import static net.peierls.expr.OperatorBuilder.*;


/**
 * Trigonometric operations on expressions.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum TrigonometricOperator implements DelegatingOp {
    /** Sine operator */
    SINE        (prefix("sin ", Math::sin)      .precedence(100)),
    /** Cosine operator */
    COSINE      (prefix("cos ", Math::cos)      .precedence(100)),
    /** Tangent operator */
    TANGENT     (prefix("tan ", Math::tan)      .precedence(100)),
    /** Arcsine operator */
    ARC_SINE    (prefix("asin ", Math::asin)    .precedence(100)),
    /** Arccosine operator */
    ARC_COSINE  (prefix("acos ", Math::acos)    .precedence(100)),
    /** Arctangent operator */
    ARC_TANGENT (prefix("atan ", Math::atan)    .precedence(100)),
    ;
    Operator delegate;
    TrigonometricOperator(Operator delegate) { this.delegate = delegate; }
    @Override public Operator delegate() { return delegate; }


    /**
     * Returns an expression representing the sine of
     * the given expression.
     */
    public static UnaryOpExpression sin(Expression expr) {
        return new UnaryOpExpression(SINE, expr);
    }

    /**
     * Returns an expression representing the cosine of
     * the given expression.
     */
    public static UnaryOpExpression cos(Expression expr) {
        return new UnaryOpExpression(COSINE, expr);
    }

    /**
     * Returns an expression representing the tangent of
     * the given expression.
     */
    public static UnaryOpExpression tan(Expression expr) {
        return new UnaryOpExpression(TANGENT, expr);
    }

    /**
     * Returns an expression representing the arc sine of
     * the given expression.
     */
    public static UnaryOpExpression asin(Expression expr) {
        return new UnaryOpExpression(ARC_SINE, expr);
    }

    /**
     * Returns an expression representing the arc cosine of
     * the given expression.
     */
    public static UnaryOpExpression acos(Expression expr) {
        return new UnaryOpExpression(ARC_COSINE, expr);
    }

    /**
     * Returns an expression representing the arc tangent of
     * the given expression.
     */
    public static UnaryOpExpression atan(Expression expr) {
        return new UnaryOpExpression(ARC_TANGENT, expr);
    }
}
