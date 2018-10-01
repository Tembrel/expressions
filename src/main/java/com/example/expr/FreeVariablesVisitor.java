package com.example.expr;

import java.util.Map;

import java.util.stream.Stream;


/** Visitor used to implement free variable set computation. */
class FreeVariablesVisitor implements Visitor<Stream<String>> {

    private static final FreeVariablesVisitor FREE_VARS_VISITOR = new FreeVariablesVisitor();

    @Override public Stream<String> visit(ConstantExpression expr) {
        return Stream.empty();
    }

    public static FreeVariablesVisitor instance() {
        return FREE_VARS_VISITOR;
    }

    @Override public Stream<String> visit(VariableExpression expr) {
        return Stream.of(expr.varName());
    }

    @Override public Stream<String> visit(UnaryOpExpression expr) {
        return expr.subExpression().accept(this);
    }

    @Override public Stream<String> visit(BinaryOpExpression expr) {
        return Stream.concat(
            expr.leftExpression().accept(this),
            expr.rightExpression().accept(this)
        ).distinct();
    }

    @Override public Stream<String> visit(LetExpression expr) {
        Map<String, Expression> bindings = expr.bindings();
        return Stream.concat(
            // All the free variables of the subexpression
            // that aren't bound in these bindings, ...
            expr.subExpression().accept(this)
                .filter(varName -> !bindings.containsKey(varName)),
            // ... plus all the free variables of the
            // expressions used in these bindings.
            bindings.keySet().stream()
                .map(bindings::get)
                .flatMap(bindingExpr -> bindingExpr.accept(this))
        ).distinct();
    }

    @Override public Stream<String> visitUnknown(Expression expr) {
        return Stream.empty();
    }
}
