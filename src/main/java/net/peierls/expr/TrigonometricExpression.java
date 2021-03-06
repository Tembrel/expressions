package net.peierls.expr;

import static net.peierls.expr.Expression.*;
import static net.peierls.expr.TrigonometricOperator.*;


/**
 * Extends expressions with trigonometric operations.
 */
public class TrigonometricExpression extends ExtendedExpression<TrigonometricExpression> {

    public TrigonometricExpression(Expression expr) {
        super(expr, TrigonometricExpression.class);
    }

    /**
     * Returns a trigonometric expression equivalent to the
     * given expression, but supporting trigonometric operations.
     */
    public static TrigonometricExpression trigExpr(Expression expr) {
        return wrap(expr, TrigonometricExpression.class);
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


    /**
     * Returns a trigonometric expression representing the sine of this expression.
     */
    public final TrigonometricExpression sin() {
        return apply(SINE);
    }

    /**
     * Returns a trigonometric expression representing the cosine of this expression.
     */
    public final TrigonometricExpression cos() {
        return apply(COSINE);
    }

    /**
     * Returns a trigonometric expression representing the tangent of this expression.
     */
    public final TrigonometricExpression tan() {
        return apply(TANGENT);
    }

    /**
     * Returns a trigonometric expression representing the arc sine of this expression.
     */
    public final TrigonometricExpression asin() {
        return apply(ARC_SINE);
    }

    /**
     * Returns a trigonometric expression representing the arc cosine of this expression.
     */
    public final TrigonometricExpression acos() {
        return apply(ARC_COSINE);
    }

    /**
     * Returns a trigonometric expression representing the arc tangent of this expression.
     */
    public final TrigonometricExpression atan() {
        return apply(ARC_TANGENT);
    }
}
