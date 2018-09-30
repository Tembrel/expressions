package com.example.expr;

import java.util.Objects;


/**
 * An expression representing the result of a unary operation on a subexpression.
 */
public class UnaryOperationExpression extends OperationExpression {
    private final UnaryOp op;
    private final Expression expr;

    UnaryOperationExpression(UnaryOp op, Expression expr) {
        this.op = op;
        this.expr = expr;
    }

    /** The unary operation used in thie expression. */
    @Override public UnaryOp operator() {
        return op;
    }

    /** The argument of this expression's unary operation. */
    public Expression subExpression() {
        return expr;
    }

    @Override protected <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(this instanceof UnaryOperationExpression)) return false;
        UnaryOperationExpression that = (UnaryOperationExpression) obj;
        return this.op == that.op
            && this.expr.equals(that.expr);
    }

    @Override public int hashCode() {
        return Objects.hash(op, expr);
    }
}
