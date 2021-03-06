package net.peierls.expr;

import static net.peierls.expr.Expression.*;
import static net.peierls.expr.ExpressionParser.*;
import static net.peierls.expr.OperatorBuilder.*;
import static net.peierls.expr.TraceVisitor.trace;

import com.google.common.collect.*;

import java.util.*;

import one.util.streamex.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionParserTest {

    ExpressionParser parser = defaultParser();

    @Test public void parseConstant() {
        assertEquals(expr(1), parseExpr("1"));
    }

    @Test public void parseVariable() {
        assertEquals(expr("a"), parseExpr("a"));
    }

    @Test public void parseUnaryOp() {
        assertEquals(expr(2.5).negated(), parseExpr("-2.5"));
    }

    @Test public void parseBinaryOp() {
        assertEquals(expr(2.5).plus(expr("a")), parseExpr("2.5 + a"));
    }

    @Test public void parseComplex() {
        VariableExpression a = expr("a");
        VariableExpression b = expr("b");
        VariableExpression c = expr("c");
        VariableExpression d = expr("d");
        Expression subExpr = d.squareRoot()
            .where(d.varName(), b.squared().minus(expr(4).times(a).times(c)));
        assertEquals(subExpr, parseExpr(subExpr.toString()));
        Expression expected = b.negated().plus(subExpr).dividedBy(expr(2).times(a));
        Expression actual = parseExpr(expected.toString());
        assertEquals(expected.toString(), actual.toString());
        assertEquals(expected, actual);
    }

    @Test public void textEqualsFormattedParse() {
        parseThenFormat("a / b c / d");
        parseThenFormat("a / b * c / d");
        parseThenFormat("let a = 2, b = 3, c = -5 in a x^2 + b x + c");
    }

    void parseThenFormat(String expected) {
        Expression expr = parseExpr(expected);
        System.out.printf("trace of %s is:%n%s%n", expected, trace(expr));
        assertEquals(expected.replace(" * ", " "), expr.toString());
    }

    @Test public void parsingAmbiguityBetweenSquaredAndPow() {
        VariableExpression a = expr("a");
        assertEquals(a.squared(), parseExpr("a^2"));
        assertEquals(a.pow(3), parseExpr("a^3"));
        // Surprise! a^23 is parsed as a^2 * 3
        assertEquals(a.squared().times(3), parseExpr("a^23"));
        assertEquals(a.pow(23), parseExpr("a ^ 23"));
    }
}
