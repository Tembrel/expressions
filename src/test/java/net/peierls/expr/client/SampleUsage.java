package net.peierls.expr.client;

import net.peierls.expr.Expression;
import net.peierls.expr.VariableExpression;
import static net.peierls.expr.Expression.expr;
import static net.peierls.expr.TraceVisitor.trace;
import static net.peierls.expr.TrigonometricExpression.trigExpr;


/**
 * Demonstrate usage of expression framework.
 */
public class SampleUsage {

    public static void main(String... args) {

        // usage 1

        Expression expr1 = expr(1).plus(expr(2.5))
            .dividedBy(expr(3).plus(expr(4)));

        double value1 = expr1.value();

        System.out.printf("expr1 = %s%nvalue1 = %f%n", expr1, value1);

        System.out.println();

        // usage 2

        Expression expr2 = expr("a").times(expr("x").squared())
            .plus(expr("b").times(expr("x")))
            .plus(expr("c"))
            .where("a", 2, "b", 3, "c", 4)
            .where("x", 5);

        double value2 = expr2.value();

        System.out.printf("expr2 = %s%nvalue2 = %f%ntrace expr2:%n%s%n", expr2, value2, trace(expr2));

        System.out.println();


        // usage 3

        Expression expr3 = expr(2).times(trigExpr(expr("a")).sin())
            .where("a", 1);

        double value3 = expr3.value();

        System.out.printf("expr3 = %s%nvalue3 = %f%n", expr3, value3);

        System.out.println();


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
            .value();

        System.out.printf("expr4 = %s%nvalue4 = %f%ntrace expr4:%n%s%n", expr4, value4, trace(expr4));

        System.out.println();


        // usage 5

        Expression expr5 = expr("x").pow(expr("y"));
        double value5 = expr5.where("x", 3, "y", 4).value();

        System.out.printf("expr5 = %s, where x=3, y=4%nvalue5 = %f%n", expr5, value5);

        System.out.println();
    }
}