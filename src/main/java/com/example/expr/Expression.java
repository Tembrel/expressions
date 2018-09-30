package com.example.expr;

import static com.example.expr.BasicOperator.*;

import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.util.Map;
import java.util.Set;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import org.jparsec.Parser;


/**
 * A double-precision arithmetic expression on constants, variables,
 * unary and binary operations, with local binding of variables to
 * subexpressions, supporting evaluation and formatting (string
 * representation).
 */
public abstract class Expression {

    /**
     * Apply a visitor function to this expression.
     * The default implementation is to call the {@link Visitor#visitUnknown}
     * method, but subclasses should override to call the visit method on one
     * of the concrete subclasses known to the visitor interface.
     */
    protected <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnknown(this);
    }


    /**
     * Turn a string representation of an expression into an Expression instance.
     */
    public static Expression parse(String exprString) {
        return BASIC_PARSER.parse(exprString);
    }

    /**
     * Returns a parser builder on basic operators to which
     * more operators can be added fluently.
     */
    @SuppressWarnings("unchecked")
    public static Parser<Expression> parserFor(Class<? extends Enum<? extends Operator>>... operatorTypes) {
        return ExpressionParser.parserFor(
            StreamEx.of(operatorTypes)
                .prepend(LogPowerOperator.class)
                .prepend(TrigonometricOperator.class)
                .prepend(BasicOperator.class)
                .toList()
        );
    }

    @SuppressWarnings("unchecked")
    private static final Parser<Expression> BASIC_PARSER = null; //parserFor();


    /**
     * Returns the string representation of this expression.
     */
    @Override public String toString() {
        return format();
    }


    /** Returns a string representation of this expression. */
    public final String format() {
        return accept(FormattingVisitor.instance());
    }


    /**
     * Returns the value of this expression, if it has no free variables
     * or divisions by zero.
     *
     * @throws DivisionByZeroException if a division reults in positive or negative infinity or NaN
     * @throws UnboundVariableException if the expression has a free variable
     */
    public final double evaluate() {
        return accept(EvaluationVisitor.instance());
    }


    /**
     * Returns true if this expression has no free variables.
     */
    public final boolean isEvaluable() {
        return freeVariables().isEmpty();
    }


    /**
     * Returns the set of free variables in this expression.
     */
    public final Set<String> freeVariables() {
        return accept(FreeVariablesVisitor.instance())
            .collect(toImmutableSet());
    }



    /**
     * Returns a constant expression with the given value.
     */
    public static ConstantExpression expr(double value) {
        return new ConstantExpression(value);
    }

    /**
     * Returns a variable expression with the given variable name.
     */
    public static VariableExpression expr(String varName) {
        return new VariableExpression(varName);
    }

    /**
     * Returns a unary operation expression on the given operation and subexpression.
     */
    public Expression apply(UnaryOp op) {
        return new UnaryOpExpression(op, this);
    }

    /**
     * Returns a binary operation expression on the given operation and left and right subexpressions.
     */
    public Expression apply(BinaryOp op, Expression right) {
        return new BinaryOpExpression(op, this, right);
    }


    /**
     * Returns an expression equivalent to this expression but with
     * the bindings of the given map of variable names to subexpressions.
     *
     * @throws UnreferencedVariableException if a bound variable is not
     *     free in this expression
     * @throws SelfReferenceException if a bound variable is free
     *     in the expression it is bound to.
     */
    public final BoundExpression where(Map<String, Expression> bindings) {
        Set<String> freeVars = freeVariables();

        // Make sure that all of the bound variables are actually
        // free in this expression.

        if (!bindings.keySet().stream()
                .allMatch(freeVars::contains)) {
            throw new UnreferencedVariableException();
        }

        // Make sure that none of the bound variables are free
        // in the expressions they are bound to.

        if (EntryStream.of(bindings)
                .mapValues(Expression::freeVariables)
                .anyMatch(e -> e.getValue().contains(e.getKey()))) {
            throw new SelfReferenceException();
        }

        return new BoundExpression(this, bindings);
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variable bound to the given subexpression.
     */
    public final BoundExpression where(String varName, Expression subExpr) {
        return where(ImmutableMap.of(varName, subExpr));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given subexpressions.
     */
    public final BoundExpression where(String v1, Expression e1, String v2, Expression e2) {
        return where(ImmutableMap.of(v1, e1, v2, e2));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given subexpressions.
     */
    public final BoundExpression where(String v1, Expression e1, String v2, Expression e2, String v3, Expression e3) {
        return where(ImmutableMap.of(v1, e1, v2, e2, v3, e3));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variable bound to the given double precision value.
     */
    public final BoundExpression where(String varName, double value) {
        return where(varName, expr(value));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given double precision values.
     */
    public final BoundExpression where(String v1, double d1, String v2, double d2) {
        return where(v1, expr(d1), v2, expr(d2));
    }

    /**
     * Returns an expression equivalent to this expression but with
     * the named variables bound to the given double precision values.
     */
    public final BoundExpression where(String v1, double d1, String v2, double d2, String v3, double d3) {
        return where(v1, expr(d1), v2, expr(d2), v3, expr(d3));
    }


    /**
     * Returns an expression representing the sum of this
     * expression and the given argument expression.
     */
    public final Expression plus(Expression that) {
        return apply(PLUS, that);
    }

    /**
     * Returns an expression representing the sum of this
     * expression and a constant expression of the given value.
     */
    public final Expression plus(double value) {
        return apply(PLUS, expr(value));
    }

    /**
     * Returns an expression representing the sum of this
     * expression and a variable expression of the given
     * variable name.
     */
    public final Expression plus(String varName) {
        return apply(PLUS, expr(varName));
    }


    /**
     * Returns an expression representing the difference between
     * this expression and the given argument expression.
     */
    public final Expression minus(Expression that) {
        return apply(MINUS, that);
    }

    /**
     * Returns an expression representing the difference between
     * this expression and a constant expression of the given value.
     */
    public final Expression minus(double value) {
        return apply(MINUS, expr(value));
    }

    /**
     * Returns an expression representing the difference between
     * this expression and a variable expression of the given
     * variable name.
     */
    public final Expression minus(String varName) {
        return apply(MINUS, expr(varName));
    }


    /**
     * Returns an expression representing the product of
     * this expression and the given argument expression.
     */
    public final Expression times(Expression that) {
        return apply(TIMES, that);
    }

    /**
     * Returns an expression representing the product of
     * this expression and a constant expression of the given value.
     */
    public final Expression times(double value) {
        return apply(TIMES, expr(value));
    }

    /**
     * Returns an expression representing the product of
     * this expression and a variable expression of the given
     * variable name.
     */
    public final Expression times(String varName) {
        return apply(TIMES, expr(varName));
    }


    /**
     * Returns an expression representing the quotient of
     * this expression with the given argument expression.
     */
    public final Expression dividedBy(Expression that) {
        return apply(DIVIDED_BY, that);
    }

    /**
     * Returns an expression representing the quotient of
     * this expression with a constant expression of the given value.
     */
    public final Expression dividedBy(double value) {
        return apply(DIVIDED_BY, expr(value));
    }

    /**
     * Returns an expression representing the quotient of
     * this expression and a variable expression of the given
     * variable name.
     */
    public final Expression dividedBy(String varName) {
        return apply(DIVIDED_BY, expr(varName));
    }


    /**
     * Returns an expression representing the negation
     * of this expression.
     */
    public final Expression negated() {
        return apply(NEGATED);
    }

    /**
     * Returns an expression representing the square
     * of this expression.
     */
    public final Expression squared() {
        return apply(SQUARED);
    }

    /**
     * Returns an expression representing the square root
     * of this expression.
     */
    public final Expression squareRoot() {
        return apply(SQUARE_ROOT);
    }
}
