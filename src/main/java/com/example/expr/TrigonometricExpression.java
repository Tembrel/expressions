package com.example.expr;

import static com.example.expr.Expression.*;
import static com.example.expr.TrigonometricOperation.*;


/**
 * Extends expressions with trigonometric operations.
 */
public class TrigonometricExpression extends Expression {

    final Expression expr;

    TrigonometricExpression(Expression expr) {
        this.expr = expr;
    }

    /**
     * Returns a trigonometric expression equivalent to the
     * given expression, but supporting trigonometric operations.
     */
    public static TrigonometricExpression trigExpr(Expression expr) {
        if (expr instanceof TrigonometricExpression) {
            return (TrigonometricExpression) expr;
        } else {
            return new TrigonometricExpression(expr);
        }
    }

    /**
     * Returns a trigonometric expression representing the
     * given constant.
     */
    public static TrigonometricExpression trigExpr(double val) {
        return trigExpr(expr(val));
    }

    /**
     * Returns a trigonometric expression representing the
     * given variable.
     */
    public static TrigonometricExpression trigExpr(String varName) {
        return trigExpr(expr(varName));
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return expr.accept(visitor);
    }

    /**
     * Returns a trigonometric expression representing the result of
     * applying the given unary operation to this expression.
     */
    @Override public TrigonometricExpression apply(UnaryOperation op) {
        return new TrigonometricExpression(expr.apply(op));
    }

    /**
     * Returns a trigonometric expression representing the sine of this expression.
     */
    public final TrigonometricExpression sine() {
        return apply(SINE);
    }

    /**
     * Returns a trigonometric expression representing the cosine of this expression.
     */
    public final TrigonometricExpression cosine() {
        return apply(COSINE);
    }

    /**
     * Returns a trigonometric expression representing the tangent of this expression.
     */
    public final TrigonometricExpression tangent() {
        return apply(TANGENT);
    }

    /**
     * Returns a trigonometric expression representing the arc sine of this expression.
     */
    public final TrigonometricExpression arcSine() {
        return apply(ARC_SINE);
    }

    /**
     * Returns a trigonometric expression representing the arc cosine of this expression.
     */
    public final TrigonometricExpression arcCosine() {
        return apply(ARC_COSINE);
    }

    /**
     * Returns a trigonometric expression representing the arc tangent of this expression.
     */
    public final TrigonometricExpression arcTangent() {
        return apply(ARC_TANGENT);
    }
}
