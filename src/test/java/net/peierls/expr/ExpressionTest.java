package net.peierls.expr;

import static net.peierls.expr.Expression.*;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionTest {

    @Test public void constantExpression() {
        double value = 3.5;
        Expression e = expr(value);
        assertEquals(value, e.value(), 0);
        assertEquals(Double.toString(value), e.toString());
    }

    @Test public void variableExpression() {
        String var = "a";
        double value = 3.5;
        Expression e = expr(var);
        Expression bound = e.where(var, value);
        assertEquals(value, bound.value(), 0);
        assertEquals(var, e.toString());
    }

    @Test(expected=UnreferencedVariableException.class) public void unreferencedVariable() {
        Expression e = expr("b").where("a", 3, "b", 4);
    }

    @Test(expected=SelfReferenceException.class) public void selfReferencedVariable() {
        Expression e = expr("a").plus(1).where("a", expr("b").plus("a"));
    }
}
