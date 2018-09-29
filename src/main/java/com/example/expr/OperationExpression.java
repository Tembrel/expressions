package com.example.expr;

/**
 * An expression representing the result of a unary operation on a subexpression.
 */
public abstract class OperationExpression extends Expression {

    /** The unary operation used in thie expression. */
    public abstract Operator operator();
}
