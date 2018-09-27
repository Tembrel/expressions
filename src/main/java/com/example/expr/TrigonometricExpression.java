package com.example.expr;

import static com.example.expr.Expression.*;
import static com.example.expr.TrigonometricExpression.TrigonometricOperation.*;

import java.util.function.*;

/**
 * Extends expressions with trigonometric operations.
 */
public class TrigonometricExpression extends Expression {

    @SuppressWarnings("ImmutableEnumChecker")
    public enum TrigonometricOperation implements Expression.UnaryOperation {
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

    final Expression expr;

    TrigonometricExpression(Expression expr) {
        this.expr = expr;
    }

    public static TrigonometricExpression trigExpr(double val) {
        return trigExpr(expr(val));
    }

    public static TrigonometricExpression trigExpr(String varName) {
        return trigExpr(expr(varName));
    }

    public static TrigonometricExpression trigExpr(Expression expr) {
        if (expr instanceof TrigonometricExpression) {
            return (TrigonometricExpression) expr;
        } else {
            return new TrigonometricExpression(expr);
        }
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return expr.accept(visitor);
    }
    
    @Override public TrigonometricExpression apply(UnaryOperation op) {
        return new TrigonometricExpression(expr.apply(op));
    }

    public final TrigonometricExpression sine() {
        return apply(SINE);
    }

    public final TrigonometricExpression cosine() {
        return apply(COSINE);
    }

    public final TrigonometricExpression tangent() {
        return apply(TANGENT);
    }

    public final TrigonometricExpression arcSine() {
        return apply(ARC_SINE);
    }

    public final TrigonometricExpression arcCosine() {
        return apply(ARC_COSINE);
    }

    public final TrigonometricExpression arcTangent() {
        return apply(ARC_TANGENT);
    }


    /**
     * Demonstrate usage
     */
    public static void main(String... args) {

        // usage 1

        Expression expr1 = expr(1).plus(expr(2.5))
            .dividedBy(expr(3).plus(expr(4)));

        double value1 = expr1.evaluate();

        System.out.printf("expr1 = %s%nvalue1 = %f%n", expr1, value1);


        // usage 2

        Expression expr2 = expr("a").times(expr("x").squared())
            .plus(expr("b").times(expr("x")))
            .plus(expr("c"))
            .where("a", 2, "b", 3, "c", 4)
            .where("x", 5);

        double value2 = expr2.evaluate();

        System.out.printf("expr2 = %s%nvalue2 = %f%n", expr2, value2);


        // usage 3

        Expression expr3 = expr(2).times(trigExpr(expr("a")).sine())
            .where("a", 1);

        double value3 = expr3.evaluate();

        System.out.printf("expr3 = %s%nvalue3 = %f%n", expr3, value3);


        // usage 4

        // (-b + [let d = (b^2 - 4 a c) in sqrt(d)]) / (2 a)
        VariableExpression a = expr("a");
        VariableExpression b = expr("b");
        VariableExpression c = expr("c");
        VariableExpression d = expr("d");
        Expression subExpr = d.squareRoot()
            .where(d.varName(), b.squared().minus(expr(4).times(a).times(c)));
        Expression expr4 = b.negated().plus(subExpr).dividedBy(expr(2).times(a));

        double value4 = expr4
            .where("a", 1, "b", 1, "c", -2)
            .evaluate();

        System.out.printf("expr4 = %s%nvalue4 = %f%n", expr4, value4);
    }
}
