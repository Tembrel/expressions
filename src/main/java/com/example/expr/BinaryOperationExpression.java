package com.example.expr;

import java.util.Objects;


/**
 * An expression representing the result of a binary operation on two subexpressions.
 */
public class BinaryOperationExpression extends Expression {
    private final BinaryOperation operation;
    private final Expression left;
    private final Expression right;

    BinaryOperationExpression(BinaryOperation operation, Expression left, Expression right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    /** The binary operation used in thie expression. */
    public BinaryOperation operation() {
        return operation;
    }

    /** The left argument of this expression's binary operation. */
    public Expression leftExpression() {
        return left;
    }

    /** The right argument of this expression's binary operation. */
    public Expression rightExpression() {
        return right;
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(this instanceof BinaryOperationExpression)) return false;
        BinaryOperationExpression that = (BinaryOperationExpression) obj;
        return this.operation == that.operation
            && this.left.equals(that.left)
            && this.right.equals(that.right);
    }

    @Override public int hashCode() {
        return Objects.hash(operation, left, right);
    }
}
