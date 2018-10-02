package net.peierls.expr;

import java.util.Objects;


/**
 * An expression representing the result of a binary operation on two subexpressions.
 */
public class BinaryOpExpression extends OperationExpression {
    private final BinaryOp op;
    private final Expression left;
    private final Expression right;

    BinaryOpExpression(BinaryOp op, Expression left, Expression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /** The binary operation used in thie expression. */
    @Override public BinaryOp operator() {
        return op;
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
        if (!(obj instanceof BinaryOpExpression)) return false;
        BinaryOpExpression that = (BinaryOpExpression) obj;
        return this.op.equals(that.op)
            && this.left.equals(that.left)
            && this.right.equals(that.right);
    }

    @Override public int hashCode() {
        return Objects.hash(op, left, right);
    }
}
