package com.example.expr;

import static com.example.expr.TrigonometricExpression.*;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TrigonometricExpressionTest {

    @Test public void sinArcSinIsNoOp() {
        double val = 1;
        TrigonometricExpression sin = trigExpr(val).sine();
        TrigonometricExpression asin = sin.arcSine();
        assertEquals(val, asin.evaluate(), 0.0001);
    }
}
