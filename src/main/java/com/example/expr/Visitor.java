package com.example.expr;

/**
 * Visitor interface for computing values from known expression subtypes.
 * Traversal of composite expression is accomplished by calling accept
 * on subexpressions within visit method implementations.
 */
public interface Visitor<R> {
    /** Visit a constant expression. */
    R visit(ConstantExpression expr);

    /** Visit a variable expression. */
    R visit(VariableExpression expr);

    /** Visit a unary operation expression. */
    R visit(UnaryOpExpression expr);

    /** Visit a binary operation expression. */
    R visit(BinaryOpExpression expr);

    /** Visit a bound expression. */
    R visit(BoundExpression expr);

    /** Visit an expression of unknown type. */
    R visitUnknown(Expression expr);
}
