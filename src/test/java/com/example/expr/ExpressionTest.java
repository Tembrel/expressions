package com.example.expr;

import static com.example.expr.Expression.*;
import static com.example.expr.TrigonometricExpression.*;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionTest {

    @Test public void constantExpression() {
        double value = 3.5;
        Expression e = expr(value);
        assertEquals(value, e.evaluate(), 0);
        assertEquals(Double.toString(value), e.format());
    }

    @Test public void variableExpression() {
        String var = "a";
        double value = 3.5;
        Expression e = expr(var);
        Expression bound = e.where(var, value);
        assertEquals(value, bound.evaluate(), 0);
        assertEquals(var, e.format());
    }
    
    @Test public void sinArcSinIsNoOp() {
        double val = 1;
        TrigonometricExpression sin = trigExpr(val).sine();
        TrigonometricExpression asin = sin.arcSine();
        assertEquals(val, asin.evaluate(), 0.0001);
    }
}
