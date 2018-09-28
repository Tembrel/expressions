package com.example.expr;

import static com.example.expr.Expression.expr;
import static com.example.expr.TrigonometricOperator.*;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TrigonometricOperationTest {

    @Test public void sinArcSinIsNoOp() {
        double val = 1;
        Expression v = expr(val);
        Expression e1 = sin(v);
        Expression e2 = asin(e1);
        assertEquals(val, e2.evaluate(), 0.0001);
    }
}
