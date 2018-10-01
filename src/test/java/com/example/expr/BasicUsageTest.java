package com.example.expr;

import static com.example.expr.Expression.expr;

import com.google.common.collect.*;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

public class BasicUsageTest {
    
    @Test public void basicUsage() {
        // Use the static `expr` methods to create atomic expressions.
        // We statically import `expr` above.

        // Constant expression
        ConstantExpression constExpr = expr(2.5);
        assertEquals(2.5, constExpr.value(), 0);

        // Variable expression
        VariableExpression varExpr = expr("a");
        assertEquals("a", varExpr.varName());

        // Use the `apply` methods to apply operators to build
        // unary and binary operations, or use the equivalent
        // built-in shortcut methods, which have overloadings
        // to accept `double` or `String` arguments in place of
        // `Expression`, avoiding an explicit wrapping step.

        Expression sumExpr1 = constExpr.apply(BasicOperator.PLUS, varExpr);
        Expression sumExpr2 = expr(2.5).plus("a");
        assertEquals(sumExpr1, sumExpr2);

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

        // Or, nearly equivalently:
        Expression quadFullyBound2 = quadWithBoundCoefficients
            .where("x", 1);

        // Find the free variables of an expression:

        assertTrue(constExpr.freeVariables().isEmpty());
        assertEquals(ImmutableSet.of("x"), quadWithBoundCoefficients.freeVariables());

        // You can evaluate an expression with no free variables, producing
        // a `double` value or throwing `DivisionByZeroException` if a division by zero
        // is encountered during the evaluation.
        
        // Evaluating an expression with free variables throws `UnboundVariableException`.
        //You can test whether an expression has no free variables with `isEvaluable`.

        assertTrue(quadFullyBound.isEvaluable());
        assertEquals(9.0, quadFullyBound.evaluate(), 0);

        // Format an expression to a string representation with `format()`.
        // `Expression.toString` is an alias for `format()`.

        assertEquals("2.5 + a", sumExpr1.format());
        assertEquals("let a = 3 in 2.5 + a", sumExpr1.where("a", 3).toString());
        
        // Parse expressions on the built-in operators with `Expression.parse`.
        
        assertEquals(sumExpr1, Expression.parse("2.5 + a"));
    }
}
