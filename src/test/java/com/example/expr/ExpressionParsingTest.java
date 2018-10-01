package com.example.expr;

import static com.example.expr.Expression.*;
import static com.example.expr.ExpressionParsing.*;
import static com.example.expr.TraceVisitor.trace;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.jparsec.Parser;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionParsingTest {

    Parser<Expression> parser = parser();

    @Test public void parseConstant() {
        assertEquals(expr(1), parser.parse("1"));
    }

    @Test public void parseVariable() {
        assertEquals(expr("a"), parser.parse("a"));
    }

    @Test public void parseUnaryOp() {
        assertEquals(expr(2.5).negated(), parser.parse("-2.5"));
    }

    @Test public void parseBinaryOp() {
        assertEquals(expr(2.5).plus(expr("a")), parser.parse("2.5 + a"));
    }

    @Test public void parseComplex() {
        VariableExpression a = expr("a");
        VariableExpression b = expr("b");
        VariableExpression c = expr("c");
        VariableExpression d = expr("d");
        Expression subExpr = d.squareRoot()
            .where(d.varName(), b.squared().minus(expr(4).times(a).times(c)));
        assertEquals(subExpr, parser.parse(subExpr.format()));
        Expression expected = b.negated().plus(subExpr).dividedBy(expr(2).times(a));
        Expression actual = parser.parse(expected.format());
        assertEquals(expected.format(), actual.format());
        assertEquals(expected, actual);
    }

    @Test public void textEqualsFormattedParse() {
        parseThenFormat("a / b c / d");
        parseThenFormat("a / b * c / d");
        parseThenFormat("let a = 2, b = 3, c = -5 in a x^2 + b x + c");
    }

    void parseThenFormat(String expected) {
        Expression expr = parser.parse(expected);
        System.out.printf("trace of %s is:%n%s%n", expected, trace(expr));
        assertEquals(expected.replace(" * ", " "), expr.format());
    }
}
