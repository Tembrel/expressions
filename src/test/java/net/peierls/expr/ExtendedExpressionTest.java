package net.peierls.expr;

import static net.peierls.expr.Expression.*;
import static net.peierls.expr.ExpressionParser.*;
import static net.peierls.expr.OperatorBuilder.*;
import static net.peierls.expr.TrigonometricExpression.*;
import static net.peierls.expr.TrigonometricOperator.*;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ExtendedExpressionTest {

    @Test public void differentWaysOfUsingExtendedExpressions() {
        Expression sinA1 = sin(expr("a"));
        Expression sinA2 = trigExpr("a").sin();
        // They print the same, but they aren't equal.
        assertEquals(sinA1.toString(), sinA2.toString());
        assertFalse(sinA1.equals(sinA2));
    }

    enum MyOp implements DelegatingOperator {
        PI_R_SQUARED(prefix("pi_r_2 ", v -> Math.PI * v * v).precedence(100)),
        CUBED(postfix("^^^", v -> v * v * v).precedence(100)),
        ;
        final Operator delegate;
        MyOp(Operator delegate) { this.delegate = delegate; }
        @Override public Operator delegate() { return delegate; }
    }

    static class MyExpression extends ExtendedExpression<MyExpression> {
        // Constructor, required
        public MyExpression(Expression expr) {
            super(expr, MyExpression.class);
        }
        // Static factory, recommended
        public static MyExpression myExpr(Expression expr) {
            return wrap(expr, MyExpression.class);
        }
        // Convenience static factory, recommended
        public static MyExpression myExpr(double val) {
            return wrap(val, MyExpression.class);
        }
        // Convenience static factory, recommended
        public static MyExpression myExpr(String var) {
            return wrap(var, MyExpression.class);
        }
        // Operation instance method, the reason for extending
        public final MyExpression piRSquared() {
            return apply(MyOp.PI_R_SQUARED);
        }
        // Operation instance method, the reason for extending
        public final MyExpression cubed() {
            return apply(MyOp.CUBED);
        }
    }

    @Test public void parsePiRSquared() {
        Expression expected = expr(2).apply(MyOp.PI_R_SQUARED);
        assertEquals(expected, Expression.of("pi_r_2 2", MyOp.class));
    }

    @Test public void parsePiRSquaredTreatedAsVariableWhenUnknownToParser() {
        Expression expected = expr("pi_r_2").times(2);
        assertEquals(expected, Expression.of("pi_r_2 2"));
    }

    @Test public void parseCubed() {
        Expression expected = expr(2).apply(MyOp.CUBED);
        assertEquals(expected, Expression.of("2^^^", MyOp.class));
    }

    @Test(expected=ExpressionParsingException.class) public void parseCubedThrowsExceptionWhenUnknownToParser() {
        Expression.of("2^^^");
    }
}
