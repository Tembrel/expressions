package net.peierls.expr;

import static net.peierls.expr.Expression.expr;
import static net.peierls.expr.Expression.parseExpr;
import com.google.common.collect.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class BasicUsageTest {
    @Test public void basicUsage() {
        // Use the static `expr` methods to create atomic expressions.
        // We statically import `expr` above.

        // Constant expressions

        ConstantExpression constExpr = expr(2.5);

        assertEquals(2.5, constExpr.value(), 0);


        // Variable expressions

        VariableExpression varExpr = expr("a");

        assertEquals("a", varExpr.varName());


        // Operation expressions

        // Use the `apply` methods to apply operators to build
        // unary and binary operations, or use the equivalent
        // built-in shortcut methods, which have overloadings
        // to accept `double` or `String` arguments in place of
        // `Expression`, avoiding an explicit wrapping step.

        Expression sumExpr1 = constExpr.apply(BasicOperator.PLUS, varExpr);
        Expression sumExpr2 = expr(2.5).plus("a");

        assertEquals(sumExpr1, sumExpr2);


        // Bound expressions ("let x = ..., y = ... in ...")

        // Use the `where` methods to bind variables in one expression
        // to other expressions. For more than three bindings, pass
        // a `Map<String, Expression>`.

        Expression quadratic = expr("a").times(expr("x").squared())
            .plus(expr("b").times("x"))
            .plus("c");
        Expression quadWithBoundCoefficients = quadratic
            .where("a", 2, "b", 3, "c", 4);

        Map<String, Expression> bindings = new HashMap<>();
        bindings.put("a", expr(2));
        bindings.put("b", expr(3));
        bindings.put("c", expr(4));
        bindings.put("x", expr(1));
        Expression quadFullyBound = quadratic.where(bindings);

        // Similarly, but not equivalently:
        Expression quadFullyBound2 = quadWithBoundCoefficients.where("x", 1);

        assertFalse(quadFullyBound.equals(quadFullyBound2));
        assertEquals(quadFullyBound.value(), quadFullyBound2.value(), 0);

        // optValue returns empty value instead of throwing:
        assertEquals(42.0, expr(1).dividedBy(0).optValue().orElse(42), 0);
        assertEquals(66.6, expr(1).plus("a").optValue().orElse(66.6), 0);


        // Free variables of an expression

        assertTrue(constExpr.freeVariables().isEmpty());
        assertEquals(ImmutableSet.of("x"), quadWithBoundCoefficients.freeVariables());


        // Evaluating expressions

        // You can evaluate an expression with no free variables, producing
        // a `double` value or throwing `DivisionByZeroException` if a division by zero
        // is encountered during the evaluation.
        //
        // Evaluating an expression with free variables throws `UnboundVariableException`.
        // You can test whether an expression has no free variables with `evaluable`.

        assertTrue(quadFullyBound.evaluable());
        assertEquals(9.0, quadFullyBound.value(), 0);

        // Formatting expressions

        // Format an expression to a string representation with `toString()`.

        assertEquals("2.5 + a", sumExpr1.toString());
        assertEquals("let a = 3 in 2.5 + a", sumExpr1.where("a", 3).toString());


        // Parsing expressions

        // Parse expressions on the built-in operators with `parseExpr`.

        assertEquals(sumExpr1, parseExpr("2.5 + a"));
        assertEquals(sumExpr1.where("a", 3), parseExpr("let a = 3 in 2.5 + a"));
    }
}
