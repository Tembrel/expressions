package com.example.expr;

/**
 * Visitor interface for computing values from known expression subtypes.
 * Traversal of composite expression is accomplished by calling
 * {@link Expression#accept Expression.accept(Vistor<R>)}
 * on subexpressions within visit method implementations.
 */
public interface Visitor<R> {
    /** Apply this visitor function to the given constant expression. */
    R visit(ConstantExpression expr);

    /** Apply this visitor function to the given variable expression. */
    R visit(VariableExpression expr);

    /** Apply this visitor function to the given unary operation expression. */
    R visit(UnaryOpExpression expr);

    /** Apply this visitor function to the given binary operation expression. */
    R visit(BinaryOpExpression expr);

    /** Apply this visitor function to the given bound expression. */
    R visit(LetExpression expr);

    /** Apply this visitor function to the given expression of unknown type. */
    R visitUnknown(Expression expr);
}
